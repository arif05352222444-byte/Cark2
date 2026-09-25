package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import kotlin.math.PI
import kotlin.math.sin

private val GoldTextBrush = Brush.verticalGradient(
    0f to Color(0xFFFFF6C2),
    0.35f to Color(0xFFFFD84A),
    0.7f to Color(0xFFFFA91F),
    1f to Color(0xFFE07A00),
)

/** Altın, gölgeli başlık stili (tek katman). */
@OptIn(ExperimentalTextApi::class)
fun goldTextStyle(size: TextUnit) = TextStyle(
    brush = GoldTextBrush,
    fontSize = size,
    fontFamily = GameFont,
    shadow = Shadow(Color(0xCC3A1E00), Offset(0f, 5f), 8f),
)

/**
 * 3D altın yazı: koyu kontur (8 yönde kaydırılmış kopya) + derinlik gölgesi + altın gradyan üst katman.
 * Blur/shader yok, sadece birkaç Text katmanı.
 */
@OptIn(ExperimentalTextApi::class)
@Composable
fun GoldText(text: String, fontSize: TextUnit, modifier: Modifier = Modifier) {
    val outline = (fontSize.value * 0.05f).coerceIn(1.2f, 3.5f)
    val depth = (fontSize.value * 0.09f).coerceIn(2f, 6f)
    val plain = TextStyle(fontSize = fontSize, fontFamily = GameFont)
    Box(modifier) {
        // Derinlik (alt koyu katman)
        Text(text, style = plain.copy(color = Color(0xFF7A3C00)), modifier = Modifier.offset(0.dp, depth.dp))
        // Kontur
        for ((dx, dy) in OUTLINE_DIRS) {
            Text(text, style = plain.copy(color = Color(0xFF5A2A00)), modifier = Modifier.offset((dx * outline).dp, (dy * outline).dp))
        }
        // Ana altın
        Text(text, style = plain.copy(brush = GoldTextBrush))
    }
}

private val OUTLINE_DIRS = listOf(-1f to 0f, 1f to 0f, 0f to -1f, 0f to 1f, -0.7f to -0.7f, 0.7f to -0.7f, -0.7f to 0.7f, 0.7f to 0.7f)

/**
 * AİLE ÇARKI tabelası. Çevresindeki ampuller yavaş bir dalga halinde yanıp söner (ağır, enerjik),
 * tabelanın dış halesi de hafifçe nefes alır. Animasyon sadece çizim aşamasında okunur →
 * her karede recomposition yok.
 */
@Composable
fun TitleMarquee(text: String, modifier: Modifier = Modifier, fontSize: TextUnit = 40.sp) {
    val transition = rememberInfiniteTransition(label = "marquee")
    val wave by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4200, easing = LinearEasing), RepeatMode.Restart),
        label = "bulbWave",
    )
    val breathe by transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2600), RepeatMode.Reverse),
        label = "glowBreathe",
    )
    Box(
        modifier
            .drawBehind {
                val r = size.height / 2f
                val bulbR = (size.height * 0.035f).coerceAtLeast(2f)
                // Dış hale (nefes alan)
                for (i in 3 downTo 1) {
                    val g = i * size.height * 0.05f
                    drawRoundRect(
                        AppColors.Gold.copy(alpha = 0.07f * breathe * (4 - i)),
                        Offset(-g, -g), Size(size.width + g * 2, size.height + g * 2), CornerRadius(r + g),
                    )
                }
                // Gövde
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color(0xFF1E3AB8), Color(0xFF0B1766), Color(0xFF081050))),
                    cornerRadius = CornerRadius(r),
                )
                // Altın çerçeve (iki katlı)
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color(0xFFFFE58A), Color(0xFFFFB321), Color(0xFFB86A00))),
                    cornerRadius = CornerRadius(r), style = Stroke(size.height * 0.07f),
                )
                val inset = size.height * 0.15f
                drawRoundRect(
                    Color(0x66FFD66B),
                    Offset(inset, inset), Size(size.width - inset * 2, size.height - inset * 2),
                    CornerRadius(r - inset), style = Stroke(size.height * 0.012f),
                )
                // Ampuller: üst ve alt sıra + yan yaylar, yavaş ilerleyen dalga
                val n = (size.width / (size.height * 0.16f)).toInt().coerceIn(12, 40)
                val edge = size.height * 0.075f
                val straight = size.width - size.height
                fun bulb(x: Float, y: Float, idx: Int) {
                    val phase = (idx.toFloat() / n) * 3f - wave * 3f
                    val a = 0.35f + 0.65f * ((sin(phase * 2f * PI.toFloat()) + 1f) / 2f)
                    drawCircle(Color(0xFFFFC24A).copy(alpha = a * 0.35f), bulbR * 2.2f, Offset(x, y))
                    drawCircle(Color(0xFFFFF4D0).copy(alpha = a), bulbR, Offset(x, y))
                }
                for (i in 0 until n) {
                    val x = r + straight * (i + 0.5f) / n
                    bulb(x, edge, i)
                    bulb(x, size.height - edge, n - i)
                }
                val side = 7
                for (i in 0 until side) {
                    val ang = PI.toFloat() / 2f + PI.toFloat() * (i + 0.5f) / side
                    val rr = r - edge
                    bulb(r + rr * kotlin.math.cos(ang), r + rr * sin(ang), i)
                    bulb(size.width - r - rr * kotlin.math.cos(ang), r + rr * sin(ang), i + n / 2)
                }
            }
            .padding(horizontal = (fontSize.value * 1.1f).dp, vertical = (fontSize.value * 0.32f).dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            GoldStar((fontSize.value * 0.75f).dp)
            Spacer(Modifier.width((fontSize.value * 0.3f).dp))
            GoldText(text, fontSize)
            Spacer(Modifier.width((fontSize.value * 0.3f).dp))
            GoldStar((fontSize.value * 0.75f).dp)
        }
    }
}

/** Altın yıldız (font bağımsız). */
@Composable
fun GoldStar(size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val c = center
        val r = this.size.minDimension / 2f
        val path = Path().apply {
            for (i in 0 until 10) {
                val a = -PI / 2 + i * PI / 5
                val rr = if (i % 2 == 0) r else r * 0.45f
                val x = c.x + (rr * kotlin.math.cos(a)).toFloat()
                val y = c.y + (rr * sin(a)).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
        drawPath(path, Color(0xFF5A2A00), style = Stroke(r * 0.18f))
        drawPath(path, GoldTextBrush)
    }
}
