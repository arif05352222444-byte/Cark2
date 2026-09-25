package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.domain.model.SegmentType
import com.ailecarki.tv.domain.model.WheelSegment
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import kotlin.math.cos
import kotlin.math.sin

private fun segmentColor(seg: WheelSegment): Color = when (seg.type) {
    SegmentType.BANKRUPT -> Color(0xFF14141C)
    SegmentType.JOKER -> Color(0xFFFFC21F)
    SegmentType.LOSE_TURN -> AppColors.Cyan
    SegmentType.DOUBLE -> AppColors.Fuchsia
    SegmentType.POINTS -> AppColors.Wheel[seg.colorIndex % 8]
}

/**
 * Çark. Açı kuralı domain/engine/WheelEngine ile birebir aynı: dilim i, [-90 + i*sweep] açısından başlar,
 * pointer sabit olarak en üstte. Dönüş yalnızca graphicsLayer.rotationZ ile yapılır (yeniden çizim yok).
 * [highlightIndex] durduğu dilimi parlatır. Çerçeve ampulleri yavaşça yanıp söner.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun WheelView(
    segments: List<WheelSegment>,
    rotation: () -> Float,
    modifier: Modifier = Modifier,
    size: Dp = 420.dp,
    highlightIndex: Int? = null,
) {
    val measurer = rememberTextMeasurer()
    val sweep = 360f / segments.size
    val k = size.value / 420f
    val transition = rememberInfiniteTransition(label = "wheelBulbs")
    val blink by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400), RepeatMode.Reverse),
        label = "blink",
    )
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        // ---- Dönen disk
        Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = rotation() }) {
            val r = this.size.minDimension / 2f
            val c = center
            val diskR = r * 0.86f
            val topLeft = Offset(c.x - diskR, c.y - diskR)
            val arcSize = Size(diskR * 2, diskR * 2)
            segments.forEachIndexed { i, seg ->
                val base = segmentColor(seg)
                val brush = Brush.radialGradient(
                    0f to lerp(base, Color.White, 0.35f),
                    0.55f to base,
                    1f to lerp(base, Color.Black, 0.35f),
                    center = c, radius = diskR,
                )
                drawArc(brush, -90f + i * sweep, sweep, true, topLeft, arcSize)
            }
            if (highlightIndex != null && highlightIndex in segments.indices) {
                val start = -90f + highlightIndex * sweep
                drawArc(Color(0x66FFFFFF), start, sweep, true, topLeft, arcSize)
                drawArc(Color(0xFFFFF3B0), start, sweep, true, topLeft, arcSize, style = Stroke(4.dp.toPx()))
            }
            // Dilim çizgileri
            for (i in segments.indices) {
                val a = Math.toRadians((-90f + i * sweep).toDouble())
                val end = Offset(c.x + diskR * cos(a).toFloat(), c.y + diskR * sin(a).toFloat())
                drawLine(Color(0xAAFFF3C8), c, end, 1.6.dp.toPx())
            }
            drawCircle(Color(0xFFFFD66B), diskR, c, style = Stroke(2.dp.toPx()))
            // Yazılar (merkezden dışa, dilim ekseni boyunca)
            segments.forEachIndexed { i, seg ->
                val mid = -90f + (i + 0.5f) * sweep
                val label = seg.label
                val fs = when {
                    label.length >= 6 -> 14f
                    label.length >= 4 -> 19f
                    else -> 23f
                } * k
                val dark = seg.type == SegmentType.JOKER || seg.colorIndex == 5 && seg.type == SegmentType.POINTS
                val layout = measurer.measure(
                    label,
                    TextStyle(
                        color = if (dark) Color(0xFF3A1E00) else Color.White,
                        fontSize = fs.sp,
                        fontFamily = GameFont,
                        shadow = if (dark) null else Shadow(Color(0xAA000000), Offset(1.5f, 2f), 3f),
                    ),
                )
                rotate(mid, c) {
                    val x = c.x + diskR * 0.94f - layout.size.width
                    drawText(layout, topLeft = Offset(x, c.y - layout.size.height / 2f))
                }
            }
        }
        // ---- Sabit katman: parlaklık, çerçeve, ampuller, göbek, ibre
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            val c = center
            val diskR = r * 0.86f
            // Cam parlaklığı
            drawCircle(
                Brush.radialGradient(listOf(Color(0x33FFFFFF), Color.Transparent), Offset(c.x - diskR * 0.35f, c.y - diskR * 0.45f), diskR * 0.9f),
                diskR, c,
            )
            // Kalın altın çerçeve
            val ringW = r * 0.1f
            val ringR = diskR + ringW / 2f
            drawCircle(
                Brush.sweepGradient(
                    listOf(Color(0xFFFFE58A), Color(0xFFD98A00), Color(0xFFFFD24A), Color(0xFFB86A00), Color(0xFFFFE58A)),
                    c,
                ),
                ringR, c, style = Stroke(ringW),
            )
            drawCircle(Color(0xFF7A4200), diskR + ringW, c, style = Stroke(r * 0.015f))
            drawCircle(Color(0xFFFFF3C8), diskR, c, style = Stroke(r * 0.008f))
            // Ampuller (yavaş, dönüşümlü)
            val bulbs = 24
            for (i in 0 until bulbs) {
                val a = Math.toRadians(i * 360.0 / bulbs + 7.5)
                val p = Offset(c.x + ringR * cos(a).toFloat(), c.y + ringR * sin(a).toFloat())
                val on = if (i % 2 == 0) blink else 1f - blink
                val alpha = 0.45f + 0.55f * on
                drawCircle(Color(0xFFFFC24A).copy(alpha = 0.35f * alpha), r * 0.04f, p)
                drawCircle(Color(0xFFFFFBE8).copy(alpha = alpha), r * 0.022f, p)
            }
            // Altın göbek
            val hubR = r * 0.17f
            drawCircle(Color(0xFF7A4200), hubR * 1.08f, c)
            drawCircle(
                Brush.radialGradient(listOf(Color(0xFFFFF0B0), Color(0xFFFFC21F), Color(0xFFC77400)), Offset(c.x - hubR * 0.3f, c.y - hubR * 0.3f), hubR * 1.3f),
                hubR, c,
            )
            drawCircle(Color(0xFFFFE58A), hubR * 0.78f, c, style = Stroke(hubR * 0.06f))
            drawPath(starPath(c, hubR * 0.6f), Brush.verticalGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFB321)), c.y - hubR * 0.6f, c.y + hubR * 0.6f))
            drawPath(starPath(c, hubR * 0.6f), Color(0xFF8A4A00), style = Stroke(hubR * 0.05f))
            // İbre: altın damla, aşağıyı gösterir
            val pinR = r * 0.085f
            val pinC = Offset(c.x, c.y - r * 0.96f)
            val pin = Path().apply {
                moveTo(c.x - pinR * 0.95f, pinC.y + pinR * 0.35f)
                lineTo(c.x, c.y - diskR + r * 0.06f)
                lineTo(c.x + pinR * 0.95f, pinC.y + pinR * 0.35f)
                close()
            }
            drawPath(pin, Brush.verticalGradient(listOf(Color(0xFFFFE58A), Color(0xFFE08A00)), pinC.y, c.y - diskR + r * 0.06f))
            drawPath(pin, Color(0xFF7A4200), style = Stroke(r * 0.012f))
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFF6C2), Color(0xFFFFB321), Color(0xFFC77400)), Offset(pinC.x - pinR * 0.3f, pinC.y - pinR * 0.3f), pinR * 1.3f), pinR, pinC)
            drawCircle(Color(0xFF7A4200), pinR, pinC, style = Stroke(r * 0.012f))
            drawCircle(Color(0xFFFFFBE8), pinR * 0.32f, pinC)
        }
    }
}

/** Çarkın durduğu sahne kaidesi (neon halkalı). Ayrı çizilir; çark merkeze gidince kaide yerinde kalır. */
@Composable
fun WheelPodium(width: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(width, width * 0.2f)) {
        val w = this.size.width
        val h = this.size.height
        drawOval(Brush.verticalGradient(listOf(Color(0xFF1C3AB8), Color(0xFF070F40))), Offset(0f, h * 0.25f), Size(w, h * 0.75f))
        drawOval(Color(0xFF5AA8FF), Offset(0f, h * 0.25f), Size(w, h * 0.75f), style = Stroke(h * 0.04f))
        drawOval(Color(0x335AA8FF), Offset(-h * 0.1f, h * 0.15f), Size(w + h * 0.2f, h * 0.95f), style = Stroke(h * 0.1f))
        drawOval(Brush.verticalGradient(listOf(Color(0xFF2A4AD0), Color(0xFF0B1766))), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.55f))
        drawOval(Color(0xFFFFC24A), Offset(w * 0.12f, 0f), Size(w * 0.76f, h * 0.55f), style = Stroke(h * 0.03f))
        for (i in 0 until 16) {
            val a = Math.PI * (i + 0.5) / 16
            val x = w / 2f + (w * 0.46f) * cos(a).toFloat()
            val y = h * 0.625f + (h * 0.33f) * sin(a).toFloat()
            drawCircle(Color(0xFFBFE0FF), h * 0.035f, Offset(x, y))
        }
    }
}

private fun starPath(c: Offset, scale: Float): Path = Path().apply {
    for (i in 0 until 10) {
        val a = Math.toRadians(-90.0 + i * 36.0)
        val rr = if (i % 2 == 0) scale else scale * 0.45f
        val x = c.x + (rr * cos(a)).toFloat()
        val y = c.y + (rr * sin(a)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}
