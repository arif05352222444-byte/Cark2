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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.model.SegmentType
import com.ailecarki.tv.domain.model.WheelSegment
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/** Dönen diskin yarıçapı / görünüm yarıçapı. wheel_frame.webp'in şeffaf iç dairesi ~0,80–0,82 → disk çerçevenin altına biraz girer. */
private const val DISK_RATIO = 0.83f
private const val HUB_RATIO = 0.48f       // yeni 3D göbek biraz daha tok görünür
private const val POINTER_H_RATIO = 0.30f // geniş 3D ibre için daha kısa yükseklik
/** Ekranlarda çarkın durduğu varsayılan açı (100 dilimi ibrenin altında). Yazılar bu konumda dik durur. */
const val WHEEL_REST_ROTATION = -15f
/** Yazıların yerleşebileceği halka (görünüm yarıçapına oran): göbeğin dışı … çerçevenin içi. */
private const val LABEL_INNER = 0.24f
private const val LABEL_OUTER = 0.77f
/** Büyük harflerde satır kutusunun üst/alt boşluğu harfe ait değil → sığdırmada kutu yüksekliğinin bu oranı esas alınır. */
private const val GLYPH_HEIGHT = 0.8f

private fun segmentColor(seg: WheelSegment): Color = when (seg.type) {
    SegmentType.BANKRUPT -> Color(0xFF14141C)
    SegmentType.JOKER -> Color(0xFFFFC21F)
    SegmentType.LOSE_TURN -> AppColors.Cyan
    SegmentType.DOUBLE -> AppColors.Fuchsia
    SegmentType.POINTS -> AppColors.Wheel[seg.colorIndex % 8]
}

private fun darkLabel(seg: WheelSegment) =
    seg.type == SegmentType.JOKER || (seg.type == SegmentType.POINTS && seg.colorIndex == 5)

/** "SIRA GEÇ" iki satır (dik yazıldığı için dilime sığar; sığmazsa otomatik küçülür). */
private fun wheelLabel(seg: WheelSegment) = seg.label.replace(' ', '\n')

/**
 * Dik (yatay) duran bir yazı kutusunun (w×h) dilim içine tamamen sığdığı merkez yarıçapını bulur.
 * Dilim: açı [phi ± half], halka [inner, outer]. Sığan tüm yarıçapların ortası döner (yazı dilimin ortasında);
 * hiç sığmıyorsa null. Kutu köşelerinin hepsi dilim içinde olmalı → yazı asla yan dilime taşmaz.
 */
internal fun fitLabelRadius(w: Float, h: Float, phiDeg: Float, halfDeg: Float, inner: Float, outer: Float, marginDeg: Float = 2f): Float? {
    val phi = Math.toRadians(phiDeg.toDouble())
    var lo = Float.NaN
    var hi = Float.NaN
    val steps = 60
    for (i in 0..steps) {
        val rho = inner + (outer - inner) * i / steps
        val cx = rho * cos(phi).toFloat()
        val cy = rho * sin(phi).toFloat()
        var good = true
        loop@ for (dx in floatArrayOf(-w / 2f, w / 2f)) for (dy in floatArrayOf(-h / 2f, h / 2f)) {
            val x = cx + dx
            val y = cy + dy
            val d = kotlin.math.hypot(x, y)
            if (d < inner || d > outer) { good = false; break@loop }
            val a = Math.toDegrees(kotlin.math.atan2(y.toDouble(), x.toDouble()))
            val diff = ((a - phiDeg + 540.0) % 360.0) - 180.0
            if (kotlin.math.abs(diff) > halfDeg - marginDeg) { good = false; break@loop }
        }
        if (good) {
            if (lo.isNaN()) lo = rho
            hi = rho
        }
    }
    return if (lo.isNaN()) null else (lo + hi) / 2f
}

/**
 * Çark. Açı kuralı domain/engine/WheelEngine ile birebir aynı: dilim i, [-90 + i*sweep] açısından başlar,
 * ibre sabit olarak en üstte. Dönüş yalnızca graphicsLayer.rotationZ ile yapılır (her karede yeniden çizim yok).
 *
 * Görsel katmanlar:
 *  - Dönen disk: dilimler + yazılar (kodla çizilir → WheelConfig ile her zaman aynı)
 *  - Sabit: altın çerçeve + ampuller (wheel_frame.webp, yavaşça parlayıp söner), göbek (wheel_hub.webp),
 *    ibre (wheel_pointer.webp)
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
    val frame = ImageBitmap.imageResource(R.drawable.wheel_frame)
    val hub = ImageBitmap.imageResource(R.drawable.wheel_hub)
    val pointer = ImageBitmap.imageResource(R.drawable.wheel_pointer)
    val sweep = 360f / segments.size
    val k = size.value / 420f
    val transition = rememberInfiniteTransition(label = "wheelGlow")
    val glow by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse),
        label = "glow",
    )
    Box(modifier.size(size), contentAlignment = Alignment.Center) {
        // ---- Dönen disk
        Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = rotation() }) {
            val r = this.size.minDimension / 2f
            val c = center
            val diskR = r * DISK_RATIO
            val topLeft = Offset(c.x - diskR, c.y - diskR)
            val arcSize = Size(diskR * 2, diskR * 2)
            segments.forEachIndexed { i, seg ->
                val base = segmentColor(seg)
                val brush = Brush.radialGradient(
                    0f to lerp(base, Color.White, 0.30f),
                    0.6f to base,
                    1f to lerp(base, Color.Black, 0.30f),
                    center = c, radius = diskR,
                )
                drawArc(brush, -90f + i * sweep, sweep, true, topLeft, arcSize)
            }
            if (highlightIndex != null && highlightIndex in segments.indices) {
                val start = -90f + highlightIndex * sweep
                drawArc(Color.White.copy(alpha = 0.12f + 0.18f * glow), start, sweep, true, topLeft, arcSize)
                drawArc(
                    Color(0xFFFFE08A).copy(alpha = 0.72f + 0.24f * glow),
                    start, sweep, true, topLeft, arcSize, style = Stroke((3.8f + 1.4f * glow).dp.toPx()),
                )
            }
            // Net altın dilim sınırları
            for (i in segments.indices) {
                val a = Math.toRadians((-90f + i * sweep).toDouble())
                val end = Offset(c.x + diskR * cos(a).toFloat(), c.y + diskR * sin(a).toFloat())
                drawLine(Color(0xFF6C3900), c, end, 3.2.dp.toPx() * k)
                drawLine(Color(0xFFFFE08A), c, end, 1.45.dp.toPx() * k)
            }
            // Yazılar: dinlenme konumunda DİK, dilimin ortasında, büyük ve kalın; dilime sığmazsa kademeli küçülür.
            segments.forEachIndexed { i, seg ->
                val mid = -90f + (i + 0.5f) * sweep
                val label = wheelLabel(seg)
                val longest = label.split('\n').maxOf { it.length }
                val base = when {
                    '\n' in label -> 22f
                    longest >= 5 -> 24f
                    longest == 4 -> 30f
                    else -> 34f
                } * k
                val dark = darkLabel(seg)
                val phiWorld = mid + WHEEL_REST_ROTATION
                var scale = 1f
                var layout = measurer.measure(label, labelStyle(base, dark))
                var rho = fitLabelRadius(layout.size.width.toFloat(), layout.size.height * GLYPH_HEIGHT, phiWorld, sweep / 2f, r * LABEL_INNER, r * LABEL_OUTER)
                while (rho == null && scale > 0.5f) {
                    scale -= 0.06f
                    layout = measurer.measure(label, labelStyle(base * scale, dark))
                    rho = fitLabelRadius(layout.size.width.toFloat(), layout.size.height * GLYPH_HEIGHT, phiWorld, sweep / 2f, r * LABEL_INNER, r * LABEL_OUTER)
                }
                val rr = rho ?: (r * (LABEL_INNER + LABEL_OUTER) / 2f)
                val a = Math.toRadians(mid.toDouble())
                val lc = Offset(c.x + rr * cos(a).toFloat(), c.y + rr * sin(a).toFloat())
                // Disk, graphicsLayer ile WHEEL_REST_ROTATION kadar döndürülüyor → yazıyı ters yönde çevir ki dik dursun.
                rotate(-WHEEL_REST_ROTATION, lc) {
                    drawText(layout, topLeft = Offset(lc.x - layout.size.width / 2f, lc.y - layout.size.height / 2f))
                }
            }
        }
        // ---- Sabit katman: çerçeve (parlayan), göbek, ibre
        Canvas(Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2f
            val c = center
            val diskR = r * DISK_RATIO
            // Cam parlaklığı
            drawCircle(
                Brush.radialGradient(listOf(Color(0x30FFFFFF), Color.Transparent), Offset(c.x - diskR * 0.35f, c.y - diskR * 0.45f), diskR * 0.9f),
                diskR, c,
            )
            // Hafif iç gölge (disk kenarı derinlik)
            drawCircle(
                Brush.radialGradient(0.82f to Color.Transparent, 1f to Color(0x66000000), center = c, radius = diskR),
                diskR, c,
            )
            val frameSide = (r * 2f).roundToInt()
            val frameTopLeft = IntOffset((c.x - r).roundToInt(), (c.y - r).roundToInt())
            drawBitmap(frame, frameTopLeft, IntSize(frameSide, frameSide))
            // Ampuller yavaşça parlayıp söner: çerçevenin aydınlatılmış kopyası üstüne eklenir
            drawBitmap(frame, frameTopLeft, IntSize(frameSide, frameSide), alpha = 0.04f + 0.20f * glow, blend = BlendMode.Plus)
            // Göbek
            val hubSide = (r * HUB_RATIO).roundToInt()
            drawBitmap(hub, IntOffset((c.x - hubSide / 2f).roundToInt(), (c.y - hubSide / 2f).roundToInt()), IntSize(hubSide, hubSide))
            // İbre: ucu diskin üst kenarının biraz içinde
            val ph = r * POINTER_H_RATIO
            val pw = ph * (pointer.width.toFloat() / pointer.height.toFloat())
            val tipY = c.y - diskR + r * 0.07f
            drawBitmap(
                pointer,
                IntOffset((c.x - pw / 2f).roundToInt(), (tipY - ph).roundToInt()),
                IntSize(pw.roundToInt(), ph.roundToInt()),
            )
        }
    }
}

private fun DrawScope.drawBitmap(
    image: ImageBitmap,
    topLeft: IntOffset,
    size: IntSize,
    alpha: Float = 1f,
    blend: BlendMode = BlendMode.SrcOver,
) {
    drawImage(
        image = image,
        srcOffset = IntOffset.Zero,
        srcSize = IntSize(image.width, image.height),
        dstOffset = topLeft,
        dstSize = size,
        alpha = alpha,
        blendMode = blend,
        filterQuality = FilterQuality.Medium,
    )
}

/** Çarkın durduğu sahne kaidesi (podium.webp). Çark merkeze gidince kaide yerinde kalır. */
@Composable
fun WheelPodium(width: Dp, modifier: Modifier = Modifier) {
    val podium = ImageBitmap.imageResource(R.drawable.podium)
    val h = width * (podium.height.toFloat() / podium.width)
    Canvas(modifier.size(width, h)) {
        drawBitmap(podium, IntOffset.Zero, IntSize(size.width.roundToInt(), size.height.roundToInt()))
    }
}

private fun labelStyle(fs: Float, dark: Boolean) = TextStyle(
    color = if (dark) Color(0xFF3A1E00) else Color.White,
    fontSize = fs.sp,
    lineHeight = fs.sp,
    fontFamily = GameFont,
    textAlign = TextAlign.Center,
    shadow = if (dark) Shadow(Color(0x66FFFFFF), Offset(0f, 1.5f), 2f) else Shadow(Color(0xDD1A0B00), Offset(2f, 3f), 5f),
)
