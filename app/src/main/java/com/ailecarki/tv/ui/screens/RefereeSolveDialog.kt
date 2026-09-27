package com.ailecarki.tv.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.Gravity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.content.ContextCompat
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.engine.AnswerNormalizer
import com.ailecarki.tv.domain.engine.TurkishAlphabet
import com.ailecarki.tv.ui.components.GameIconKind
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.neonPanel
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import kotlinx.coroutines.delay

private const val AUTO_HIDE_MS = 3000L

/** Hakem paneli ölçüleri. Ekranlar bu kadar alanı sağda boş bırakır → panel bulmacanın üstüne binmez. */
val REFEREE_PANEL_WIDTH = 310.dp
val REFEREE_PANEL_END = 22.dp
val REFEREE_RESERVED_WIDTH = REFEREE_PANEL_WIDTH + REFEREE_PANEL_END + 14.dp

/**
 * Hızlı çözüm: ana oyun görünür kalır, sağ altta yalnızca hakemin yakından okuyabileceği panel açılır.
 * SpeechRecognizer yalnızca söylenen cevabı yazıya dökerek yardımcı olur; doğru/yanlış kararını ASLA otomatik vermez.
 * Mikrofon yoksa veya izin reddedilirse manuel hakem akışı aynen devam eder.
 */
@Composable
fun RefereeSolveDialog(
    playerName: String,
    answer: String,
    onCorrect: () -> Unit,
    onWrong: () -> Unit,
    onType: () -> Unit,
    onDismiss: () -> Unit,
    secondsLeft: Int? = null,
) {
    val context = LocalContext.current
    var curtainOpen by remember { mutableStateOf(false) }
    var spokenText by remember { mutableStateOf("") }
    var listening by remember { mutableStateOf(false) }
    var listenNonce by remember { mutableIntStateOf(0) }
    var hasAudioPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val speechAvailable = remember { SpeechRecognizer.isRecognitionAvailable(context) }
    val recognizer = remember(speechAvailable) { if (speechAvailable) SpeechRecognizer.createSpeechRecognizer(context) else null }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        hasAudioPermission = granted
        if (granted) listenNonce += 1
    }

    DisposableEffect(recognizer) {
        onDispose { runCatching { recognizer?.destroy() } }
    }

    DisposableEffect(recognizer) {
        if (recognizer == null) return@DisposableEffect onDispose { }
        recognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) { listening = true }
            override fun onBeginningOfSpeech() { listening = true }
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() { listening = false }
            override fun onError(error: Int) { listening = false }
            override fun onResults(results: Bundle?) {
                listening = false
                val items = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).orEmpty()
                spokenText = items.firstOrNull()?.let(TurkishAlphabet::upper).orEmpty()
            }
            override fun onPartialResults(partialResults: Bundle?) {
                val items = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).orEmpty()
                if (items.isNotEmpty()) spokenText = TurkishAlphabet.upper(items.first())
            }
            override fun onEvent(eventType: Int, params: Bundle?) = Unit
        })
        onDispose { runCatching { recognizer.cancel() } }
    }

    fun startListening() {
        if (!speechAvailable || recognizer == null) return
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            return
        }
        spokenText = ""
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "tr-TR")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
        }
        runCatching { recognizer.startListening(intent) }.onFailure { listening = false }
    }

    LaunchedEffect(Unit) {
        if (speechAvailable) {
            delay(250)
            startListening()
        }
    }
    LaunchedEffect(listenNonce) {
        if (listenNonce > 0) {
            delay(100)
            startListening()
        }
    }
    LaunchedEffect(curtainOpen) {
        if (curtainOpen) {
            delay(AUTO_HIDE_MS)
            curtainOpen = false
        }
    }

    val openRequester = remember { FocusRequester() }
    RequestFocus(openRequester, Unit)
    val likelyMatch = spokenText.isNotBlank() && AnswerNormalizer.matches(spokenText, answer, lenient = true)

    fun decide(action: () -> Unit) {
        curtainOpen = false
        runCatching { recognizer?.cancel() }
        listening = false
        action()
    }

    Dialog(
        onDismissRequest = { decide(onDismiss) },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val view = LocalView.current
        SideEffect {
            val provider = (view as? DialogWindowProvider) ?: (view.parent as? DialogWindowProvider)
            provider?.window?.let { w ->
                w.setDimAmount(0f)
                w.setGravity(Gravity.BOTTOM or Gravity.END)
            }
        }
        Column(
            Modifier
                .padding(end = REFEREE_PANEL_END, bottom = 14.dp)
                .width(REFEREE_PANEL_WIDTH)
                .neonPanel(16.dp, glow = true, borderColor = AppColors.Gold)
                .padding(horizontal = 11.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.referee_title), color = AppColors.Gold, fontSize = 16.sp, fontFamily = GameFont)
                Spacer(Modifier.width(8.dp))
                Text(playerName.uppercase(TurkishAlphabet.LOCALE), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black, maxLines = 1)
                if (secondsLeft != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.final_time_left, secondsLeft),
                        color = if (secondsLeft <= 5) AppColors.Red else AppColors.GoldLight,
                        fontSize = 14.sp,
                        fontFamily = GameFont,
                    )
                }
            }

            // Söylenen cevap görünür; gerçek cevap perde altında kalır.
            Text("SÖYLENEN", color = AppColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Box(
                Modifier.fillMaxWidth().height(38.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF09123D)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    when {
                        spokenText.isNotBlank() -> spokenText
                        listening -> "DİNLİYORUM…"
                        speechAvailable -> "Tekrar dinlemek için MİKROFON"
                        else -> "SÖZLÜ KONTROL"
                    },
                    color = if (likelyMatch) Color(0xFF8CFF9E) else Color.White,
                    fontSize = if (spokenText.length > 18) 11.sp else 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                )
            }
            if (likelyMatch) {
                Text("Muhtemel eşleşme · kararı hakem verir", color = Color(0xFF8CFF9E), fontSize = 9.sp)
            }
            if (speechAvailable) {
                TvButton(
                    if (listening) "DİNLİYORUM…" else "MİKROFON / TEKRAR DİNLE",
                    onClick = { startListening() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !listening,
                    height = 28.dp,
                    fontSize = 11.sp,
                    corner = 8.dp,
                    icon = GameIconKind.MIC,
                )
            }

            Text(stringResource(R.string.referee_answer_label), color = AppColors.TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Box(
                Modifier.fillMaxWidth().height(46.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF050A2E)),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = curtainOpen,
                    transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                    label = "curtain",
                ) { open ->
                    if (open) {
                        Box(Modifier.fillMaxSize().padding(horizontal = 6.dp), contentAlignment = Alignment.Center) {
                            Text(
                                answer,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                maxLines = 3,
                            )
                        }
                    } else Curtain(stringResource(R.string.referee_hidden))
                }
            }
            TvButton(
                stringResource(if (curtainOpen) R.string.referee_hide else R.string.referee_open),
                { curtainOpen = !curtainOpen },
                Modifier.fillMaxWidth(),
                focusRequester = openRequester,
                height = 34.dp,
                fontSize = 14.sp,
                corner = 10.dp,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TvButton(
                    stringResource(R.string.referee_correct), { decide(onCorrect) }, Modifier.weight(1f),
                    height = 40.dp, fontSize = 16.sp, corner = 10.dp, icon = GameIconKind.CHECK, contentPadding = 8.dp,
                )
                TvButton(
                    stringResource(R.string.referee_wrong), { decide(onWrong) }, Modifier.weight(1f),
                    height = 40.dp, fontSize = 16.sp, corner = 10.dp, icon = GameIconKind.PLAY, contentPadding = 8.dp,
                )
            }
            TvButton(
                stringResource(R.string.referee_type), { decide(onType) }, Modifier.fillMaxWidth(),
                height = 28.dp, fontSize = 12.sp, corner = 8.dp, icon = GameIconKind.KEYBOARD,
            )
        }
    }
}

/** Kırmızı kadife perde + altın saçak; gerçek cevap ancak hakem istediğinde 3 saniyeliğine görünür. */
@Composable
private fun Curtain(label: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val folds = 9
            val fw = size.width / folds
            for (i in 0 until folds) {
                drawRect(
                    Brush.horizontalGradient(
                        listOf(Color(0xFF5A0010), Color(0xFFB0102A), Color(0xFF6A0014)),
                        startX = i * fw,
                        endX = (i + 1) * fw,
                    ),
                    Offset(i * fw, 0f),
                    Size(fw, size.height),
                )
            }
            drawRect(Color(0xFFFFC24A), Offset(0f, 0f), Size(size.width, size.height * 0.1f))
            for (i in 0 until 24) {
                drawCircle(Color(0xFFFFD66B), size.height * 0.035f, Offset(size.width * (i + 0.5f) / 24f, size.height * 0.13f))
            }
        }
        Text(label, color = Color(0xFFFFE58A), fontSize = 14.sp, fontFamily = GameFont)
    }
}
