package com.ailecarki.tv.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.model.GameEvent
import com.ailecarki.tv.domain.model.GameState
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import com.ailecarki.tv.ui.theme.formatScore
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

private const val BANNER_MS = 1900L

/**
 * Oyun olaylarını kısa süre büyük gösterir. Eski olayları tekrar oynatmaz.
 * - Harf bulundu: 500 → 1000 → 1500 sayımı, sonra +1.500 PUAN
 * - İflas: ekran kısa kararır + sarsıntı
 * - Doğru cevap: konfeti; 2X: fuşya ışık patlaması; Sıra geç: camgöbeği
 */
@Composable
fun EventBanner(s: GameState, modifier: Modifier = Modifier) {
    var seen by remember { mutableIntStateOf(s.eventCounter) }
    var shown by remember { mutableStateOf<GameEvent?>(null) }
    var shownKey by remember { mutableIntStateOf(0) }
    val shake = remember { Animatable(0f) }
    val dim = remember { Animatable(0f) }
    val burst = remember { Animatable(0f) }
    val count = remember { Animatable(0f) }

    LaunchedEffect(s.eventCounter) {
        if (s.eventCounter == seen) return@LaunchedEffect
        seen = s.eventCounter
        val e = s.lastEvent
        if (e == null || e is GameEvent.FinalResult) return@LaunchedEffect
        shown = e
        shownKey = s.eventCounter
        burst.snapTo(0f)
        launch { burst.animateTo(1f, tween(900, easing = FastOutSlowInEasing)) }
        if (e is GameEvent.LetterFound && e.count > 1) {
            val step = e.points / e.count
            count.snapTo(step.toFloat())
            launch { count.animateTo(e.points.toFloat(), tween(220 * (e.count - 1), easing = LinearEasing)) }
        } else if (e is GameEvent.LetterFound) {
            count.snapTo(e.points.toFloat())
        }
        if (e is GameEvent.Bankrupt) {
            launch { dim.animateTo(0.6f, tween(180)); delay(900); dim.animateTo(0f, tween(500)) }
        }
        if (e is GameEvent.Bankrupt || e is GameEvent.LetterMissing || e is GameEvent.WrongAnswer) {
            for (x in listOf(-24f, 22f, -18f, 14f, -8f, 0f)) shake.animateTo(x, tween(55))
        }
        delay(BANNER_MS)
        shown = null
    }

    val e = shown
    val next = stringResource(R.string.ev_next_turn, s.currentPlayer.name)
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // İflas karartması
        Box(Modifier.fillMaxSize().graphicsLayer { alpha = dim.value }.background(Color(0xFF02030C)))
        if (e != null) {
            val accent = bannerAccent(e)
            SparkleBurst(accent, { burst.value }, Modifier.size(520.dp))
        }
        AnimatedVisibility(
            visible = e != null,
            enter = fadeIn(tween(150)) + scaleIn(tween(240), initialScale = 0.55f),
            exit = fadeOut(tween(220)) + scaleOut(tween(220), targetScale = 0.9f),
        ) {
            if (e != null) {
                val title: String
                val sub: String?
                when (e) {
                    is GameEvent.LetterFound -> {
                        title = if (e.count > 1) "${e.letter} × ${e.count}" else stringResource(R.string.ev_letters_found, e.count)
                        sub = null
                    }
                    else -> {
                        val t = bannerTexts(e, next)
                        title = t.first
                        sub = t.second
                    }
                }
                BannerCard(
                    title = title,
                    subtitle = sub,
                    accent = bannerAccent(e),
                    modifier = Modifier.graphicsLayer { translationX = shake.value * density },
                    liveScore = if (e is GameEvent.LetterFound) ({ "+" + formatScore(count.value.toInt()) + " PUAN" }) else null,
                )
            }
        }
        if (shown is GameEvent.CorrectAnswer) Confetti(trigger = shownKey)
    }
}

private fun bannerAccent(e: GameEvent): Color = when (e) {
    is GameEvent.LetterFound -> AppColors.Gold
    is GameEvent.VowelBought -> if (e.count > 0) AppColors.Gold else AppColors.Red
    is GameEvent.LetterMissing, GameEvent.Bankrupt, is GameEvent.WrongAnswer -> AppColors.Red
    GameEvent.LoseTurn -> AppColors.Cyan
    GameEvent.DoubleActivated -> AppColors.Fuchsia
    is GameEvent.CorrectAnswer -> AppColors.Green
    is GameEvent.FinalResult -> AppColors.Gold
}

@Composable
private fun bannerTexts(e: GameEvent, next: String): Pair<String, String?> = when (e) {
    is GameEvent.LetterFound -> stringResource(R.string.ev_letters_found, e.count) to
        stringResource(R.string.ev_plus_points, formatScore(e.points))
    is GameEvent.LetterMissing -> stringResource(R.string.ev_letter_missing, e.letter.toString()) to next
    is GameEvent.VowelBought ->
        (if (e.count > 0) stringResource(R.string.ev_letters_found, e.count)
        else stringResource(R.string.ev_letter_missing, e.letter.toString())) to stringResource(R.string.ev_vowel_cost, e.cost)
    GameEvent.Bankrupt -> stringResource(R.string.ev_bankrupt) to next
    GameEvent.LoseTurn -> stringResource(R.string.ev_lose_turn) to next
    GameEvent.DoubleActivated -> stringResource(R.string.ev_double) to stringResource(R.string.double_hint)
    is GameEvent.CorrectAnswer -> stringResource(R.string.ev_correct) to null
    is GameEvent.WrongAnswer -> stringResource(R.string.ev_wrong) to next
    is GameEvent.FinalResult -> "" to null
}

/** Işın patlaması: bir kez oynar (progress 0→1). */
@Composable
fun SparkleBurst(color: Color, progress: () -> Float, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val p = progress()
        if (p <= 0f || p >= 1f) return@Canvas
        val c = center
        val maxR = size.minDimension / 2f
        val alpha = (1f - p)
        for (i in 0 until 16) {
            val a = Math.toRadians(i * 22.5)
            val inner = maxR * (0.15f + 0.5f * p)
            val outer = inner + maxR * (0.12f + 0.25f * (1f - p))
            drawLine(
                color.copy(alpha = alpha * (if (i % 2 == 0) 0.9f else 0.5f)),
                Offset(c.x + inner * cos(a).toFloat(), c.y + inner * sin(a).toFloat()),
                Offset(c.x + outer * cos(a).toFloat(), c.y + outer * sin(a).toFloat()),
                strokeWidth = (if (i % 2 == 0) 6f else 3f) * density,
                cap = StrokeCap.Round,
            )
        }
        drawCircle(color.copy(alpha = 0.25f * alpha), maxR * (0.2f + 0.6f * p), c, style = Stroke(10f * density * alpha))
    }
}

/** Büyük olay kartı. [liveScore] verilirse alt satır animasyonlu sayaç olur. */
@Composable
fun BannerCard(
    title: String,
    subtitle: String?,
    accent: Color,
    modifier: Modifier = Modifier,
    liveScore: (() -> String)? = null,
) {
    Column(
        modifier
            .drawBehind {
                val r = CornerRadius(26.dp.toPx())
                for (i in 4 downTo 1) {
                    val g = i * 5f * density
                    drawRoundRect(accent.copy(alpha = 0.06f * (5 - i)), Offset(-g, -g), Size(size.width + g * 2, size.height + g * 2), CornerRadius(r.x + g))
                }
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xF7162C94), Color(0xF7070D3C))), cornerRadius = r)
                drawRoundRect(Brush.verticalGradient(listOf(Color(0x33FFFFFF), Color.Transparent), 0f, size.height * 0.4f), size = Size(size.width, size.height * 0.4f), cornerRadius = r)
                drawRoundRect(accent, cornerRadius = r, style = Stroke(4.dp.toPx()))
            }
            .padding(horizontal = 52.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (accent == AppColors.Gold) {
            GoldText(title, 54.sp)
        } else {
            Text(title, color = accent, fontSize = 54.sp, fontFamily = GameFont, textAlign = TextAlign.Center)
        }
        if (liveScore != null) {
            LiveScoreText(liveScore)
        } else if (subtitle != null) {
            Text(subtitle, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun LiveScoreText(text: () -> String) {
    Text(text(), style = goldTextStyle(34.sp))
}
