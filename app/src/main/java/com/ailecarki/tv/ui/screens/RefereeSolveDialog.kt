package com.ailecarki.tv.ui.screens

import android.view.Gravity
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import com.ailecarki.tv.R
import com.ailecarki.tv.ui.components.GameIconKind
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.neonPanel
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import kotlinx.coroutines.delay

private const val AUTO_HIDE_MS = 3000L

/**
 * Hakemli hızlı çözüm — SADECE sağ altta küçük panel. Ana ekran ve bulmaca kararmadan görünür kalır.
 * Oyuncu cevabı yüksek sesle söyler; hakem TV'ye yaklaşır, CEVABI AÇ ile gizli cevaba bakar
 * (3 sn sonra kendiliğinden gizlenir) ve TAMAM (doğru) / DEVAM (yanlış) der.
 * Geri tuşu: hiçbir şey değişmeden panel kapanır (cevap önce gizlenir).
 * Ayrı pencere (Dialog) → kumanda odağı panelde kalır.
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
    var curtainOpen by remember { mutableStateOf(false) }
    val openRequester = remember { FocusRequester() }
    RequestFocus(openRequester, Unit)
    LaunchedEffect(curtainOpen) {
        if (curtainOpen) {
            delay(AUTO_HIDE_MS)
            curtainOpen = false
        }
    }
    // Karar/çıkış öncesi cevap her zaman önce maskelenir.
    fun decide(action: () -> Unit) {
        curtainOpen = false
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
        // Pencereyi sağ alta yerleştir ve arka planı KARARTMA.
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
                .padding(end = 30.dp, bottom = 18.dp)
                .width(262.dp)
                .neonPanel(16.dp, glow = true, borderColor = AppColors.Gold)
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.referee_title), color = AppColors.Gold, fontSize = 15.sp, fontFamily = GameFont)
                Spacer(Modifier.width(8.dp))
                Text(playerName, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                if (secondsLeft != null) {
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.final_time_left, secondsLeft),
                        color = if (secondsLeft <= 5) AppColors.Red else AppColors.GoldLight,
                        fontSize = 14.sp, fontFamily = GameFont,
                    )
                }
            }
            Text(stringResource(R.string.referee_answer_label), color = AppColors.TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF050A2E)),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = curtainOpen,
                    transitionSpec = { fadeIn(tween(150)) togetherWith fadeOut(tween(150)) },
                    label = "curtain",
                ) { open ->
                    if (open) {
                        Box(Modifier.fillMaxSize().padding(horizontal = 6.dp), contentAlignment = Alignment.Center) {
                            // Bilerek küçük: hakem yakından okur, uzaktaki oyuncular okuyamaz.
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
                    } else {
                        Curtain(stringResource(R.string.referee_hidden))
                    }
                }
            }
            TvButton(
                stringResource(if (curtainOpen) R.string.referee_hide else R.string.referee_open),
                { curtainOpen = !curtainOpen },
                Modifier.fillMaxWidth(),
                focusRequester = openRequester,
                height = 34.dp,
                fontSize = 15.sp,
                corner = 10.dp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TvButton(
                    stringResource(R.string.referee_correct), { decide(onCorrect) }, Modifier.width(117.dp),
                    height = 38.dp, fontSize = 15.sp, corner = 10.dp, icon = GameIconKind.CHECK,
                )
                TvButton(
                    stringResource(R.string.referee_wrong), { decide(onWrong) }, Modifier.width(117.dp),
                    height = 38.dp, fontSize = 15.sp, corner = 10.dp, icon = GameIconKind.PLAY,
                )
            }
            TvButton(
                stringResource(R.string.referee_type), { decide(onType) }, Modifier.fillMaxWidth(),
                height = 28.dp, fontSize = 12.sp, corner = 8.dp, icon = GameIconKind.KEYBOARD,
            )
        }
    }
}

/** Kırmızı kadife perde + altın saçak (cevap maskesi). */
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
                        startX = i * fw, endX = (i + 1) * fw,
                    ),
                    Offset(i * fw, 0f), Size(fw, size.height),
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
