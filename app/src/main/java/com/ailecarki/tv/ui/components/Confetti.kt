package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import com.ailecarki.tv.ui.theme.AppColors
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Piece(val x: Float, val speed: Float, val sway: Float, val phase: Float, val color: Int, val size: Float)

/** Sınırlı süreli konfeti yağmuru. */
@Composable
fun Confetti(trigger: Any?, modifier: Modifier = Modifier, durationMs: Int = 3500, count: Int = 70) {
    val progress = remember(trigger) { Animatable(0f) }
    val pieces = remember(trigger) {
        val r = Random(trigger.hashCode())
        List(count) {
            Piece(
                x = r.nextFloat(),
                speed = 0.6f + r.nextFloat() * 0.8f,
                sway = 0.02f + r.nextFloat() * 0.04f,
                phase = r.nextFloat() * 6f,
                color = r.nextInt(AppColors.Wheel.size),
                size = 6f + r.nextFloat() * 8f,
            )
        }
    }
    LaunchedEffect(trigger) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMs, easing = LinearEasing))
    }
    Canvas(modifier.fillMaxSize()) {
        val p = progress.value
        if (p <= 0f || p >= 1f) return@Canvas
        pieces.forEach { piece ->
            val y = (-0.1f + p * piece.speed * 1.3f) * size.height
            val x = (piece.x + sin(p * 12f + piece.phase) * piece.sway) * size.width
            val s = piece.size * density / 2f
            val rotationPulse = 0.45f + 0.55f * kotlin.math.abs(sin(p * 16f + piece.phase))
            drawRect(
                AppColors.Wheel[piece.color],
                Offset(x, y),
                Size(s * rotationPulse, s * 1.6f),
                alpha = 1f - p * 0.35f,
            )
        }
    }
}

/** Kutlama yoğunluğu: tur sonu daha hafif, büyük final daha coşkulu. */
enum class CelebrationLevel { ROUND, FINAL }

/**
 * Hafif Canvas kutlama katmanı: hareketli havai fişekler + iki yanda sahne kıvılcım fıskiyeleri.
 * Video/particle engine yok; düşük parçacık sayısıyla TV Box performansını korur.
 */
@Composable
fun CelebrationLayer(
    trigger: Any?,
    level: CelebrationLevel,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "celebration")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (level == CelebrationLevel.FINAL) 2600 else 3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "fireworksPhase",
    )
    val fountain by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1250, easing = LinearEasing), RepeatMode.Restart),
        label = "fountainPhase",
    )
    val seed = trigger?.hashCode() ?: 1
    val burstCenters = remember(seed, level) {
        if (level == CelebrationLevel.FINAL) {
            listOf(0.13f to 0.22f, 0.31f to 0.16f, 0.52f to 0.21f, 0.72f to 0.15f, 0.88f to 0.24f)
        } else {
            listOf(0.18f to 0.24f, 0.50f to 0.18f, 0.82f to 0.24f)
        }
    }

    Canvas(modifier.fillMaxSize()) {
        // Hareketli sahne spotları: finalde daha geniş ve hızlı, tur sonunda daha sakin.
        val sweep = (phase * 2f * PI).toFloat()
        val beamCount = if (level == CelebrationLevel.FINAL) 5 else 3
        for (i in 0 until beamCount) {
            val originX = size.width * ((i + 1f) / (beamCount + 1f))
            val targetX = size.width * (0.5f + 0.42f * sin((sweep + i * 1.31f).toDouble()).toFloat())
            val targetY = size.height * (if (level == CelebrationLevel.FINAL) 0.78f else 0.68f)
            val half = size.width * (if (level == CelebrationLevel.FINAL) 0.055f else 0.04f)
            val path = Path().apply {
                moveTo(originX - 7f * density, 0f)
                lineTo(originX + 7f * density, 0f)
                lineTo(targetX + half, targetY)
                lineTo(targetX - half, targetY)
                close()
            }
            drawPath(path, Color(0xFF5AA8FF).copy(alpha = if (i % 2 == 0) 0.085f else 0.055f))
        }

        val palette = listOf(
            Color(0xFFFFD34D), Color(0xFFFF7A3D), Color(0xFFFF4F9A),
            Color(0xFF58C7FF), Color(0xFF8D76FF), Color.White,
        )
        val rays = if (level == CelebrationLevel.FINAL) 22 else 15
        val maxRadius = size.minDimension * if (level == CelebrationLevel.FINAL) 0.155f else 0.11f

        burstCenters.forEachIndexed { index, (nx, ny) ->
            val local = (phase + index * 0.19f) % 1f
            if (local < 0.78f) {
                val q = local / 0.78f
                val alpha = (1f - q).coerceIn(0f, 1f)
                val radius = maxRadius * (0.10f + 0.90f * q)
                val c = Offset(size.width * nx, size.height * ny)
                val color = palette[(index * 2 + seed) .let { kotlin.math.abs(it) } % palette.size]
                for (r in 0 until rays) {
                    val a = (2.0 * PI * r / rays) + index * 0.31
                    val inner = radius * 0.32f
                    val outer = radius
                    val p1 = Offset(c.x + cos(a).toFloat() * inner, c.y + sin(a).toFloat() * inner)
                    val p2 = Offset(c.x + cos(a).toFloat() * outer, c.y + sin(a).toFloat() * outer)
                    drawLine(color.copy(alpha = alpha), p1, p2, strokeWidth = (2.2f + (r % 3)) * density, cap = StrokeCap.Round)
                    if (r % 2 == 0) drawCircle(Color.White.copy(alpha = alpha * 0.9f), 2.3f * density, p2)
                }
                drawCircle(color.copy(alpha = alpha * 0.20f), radius * 0.45f, c)
            }
        }

        // Sağ ve solda hareketli sahne kıvılcım fıskiyeleri.
        val sparkCount = if (level == CelebrationLevel.FINAL) 36 else 22
        for (side in 0..1) {
            val baseX = if (side == 0) size.width * 0.055f else size.width * 0.945f
            for (i in 0 until sparkCount) {
                val local = (fountain + i.toFloat() / sparkCount) % 1f
                val y = size.height * (0.93f - 0.52f * local)
                val spread = (0.025f + 0.055f * local) * size.width
                val dir = if (side == 0) 1f else -1f
                val wave = sin((local * 10f + i * 1.7f).toDouble()).toFloat()
                val x = baseX + dir * spread * (0.25f + 0.75f * kotlin.math.abs(wave))
                val alpha = (1f - local).coerceIn(0f, 1f)
                val col = if (i % 3 == 0) Color.White else AppColors.GoldLight
                drawLine(
                    col.copy(alpha = alpha),
                    Offset(x, y),
                    Offset(x - dir * 4f * density, y + 18f * density),
                    strokeWidth = (1.5f + (i % 3)) * density,
                    cap = StrokeCap.Round,
                )
            }
        }
    }

    // Konfeti üst katmanı; turda daha az, finalde daha yoğun.
    Confetti(
        trigger = trigger,
        modifier = modifier,
        durationMs = if (level == CelebrationLevel.FINAL) 9000 else 6000,
        count = if (level == CelebrationLevel.FINAL) 190 else 110,
    )
}
