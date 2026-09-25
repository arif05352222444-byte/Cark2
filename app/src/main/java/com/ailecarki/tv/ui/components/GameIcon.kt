package com.ailecarki.tv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

enum class GameIconKind {
    PLAY, REFRESH, GEAR, DOOR, PLUS, SHUFFLE, BACK, WHEEL, KEYBOARD, BULB, CHECK,
    MIC, MUSIC, SPEAKER, BELL, LIST, COINS, TIMER, PERSON, STAR,
}

/** Bağımlılıksız, Canvas ile çizilen basit ikonlar (24x24 birim koordinat). */
@Composable
fun GameIcon(kind: GameIconKind, color: Color, modifier: Modifier = Modifier, size: Dp = 26.dp) {
    Canvas(modifier.size(size)) {
        val u = this.size.minDimension / 24f
        drawIcon(kind, color, u)
    }
}

private fun DrawScope.p(x: Float, y: Float, u: Float) = Offset(x * u, y * u)

private fun DrawScope.drawIcon(kind: GameIconKind, c: Color, u: Float) {
    val line = Stroke(width = 2.4f * u, cap = StrokeCap.Round)
    when (kind) {
        GameIconKind.PLAY -> drawPath(Path().apply {
            moveTo(6f * u, 3f * u); lineTo(20f * u, 12f * u); lineTo(6f * u, 21f * u); close()
        }, c)
        GameIconKind.BACK -> drawPath(Path().apply {
            moveTo(18f * u, 3f * u); lineTo(4f * u, 12f * u); lineTo(18f * u, 21f * u); close()
        }, c)
        GameIconKind.PLUS -> {
            drawCircle(c, 11f * u, p(12f, 12f, u))
            drawLine(Color(0xFF0B2383), p(12f, 6.5f, u), p(12f, 17.5f, u), 3f * u, StrokeCap.Round)
            drawLine(Color(0xFF0B2383), p(6.5f, 12f, u), p(17.5f, 12f, u), 3f * u, StrokeCap.Round)
        }
        GameIconKind.CHECK -> drawPath(Path().apply {
            moveTo(3.5f * u, 12.5f * u); lineTo(9.5f * u, 18.5f * u); lineTo(20.5f * u, 5.5f * u)
        }, c, style = Stroke(3.6f * u, cap = StrokeCap.Round))
        GameIconKind.REFRESH -> {
            drawArc(c, 200f, 140f, false, p(3f, 3f, u), Size(18f * u, 18f * u), style = line)
            drawArc(c, 20f, 140f, false, p(3f, 3f, u), Size(18f * u, 18f * u), style = line)
            drawPath(Path().apply { moveTo(17f * u, 2.5f * u); lineTo(21.5f * u, 6f * u); lineTo(16f * u, 8f * u); close() }, c)
            drawPath(Path().apply { moveTo(7f * u, 21.5f * u); lineTo(2.5f * u, 18f * u); lineTo(8f * u, 16f * u); close() }, c)
        }
        GameIconKind.GEAR -> {
            for (i in 0 until 8) {
                val a = Math.toRadians(i * 45.0)
                drawLine(c, p(12f + 7f * cos(a).toFloat(), 12f + 7f * sin(a).toFloat(), u),
                    p(12f + 10.5f * cos(a).toFloat(), 12f + 10.5f * sin(a).toFloat(), u), 3.6f * u, StrokeCap.Round)
            }
            drawCircle(c, 7.5f * u, p(12f, 12f, u))
            drawCircle(Color(0xFF0B2383), 3f * u, p(12f, 12f, u))
        }
        GameIconKind.DOOR -> {
            drawRoundRect(c, p(5f, 2.5f, u), Size(12f * u, 19f * u), CornerRadius(1.5f * u), style = line)
            drawCircle(c, 1.4f * u, p(14f, 12.5f, u))
            drawLine(c, p(17f, 12f, u), p(22f, 12f, u), 2.2f * u, StrokeCap.Round)
        }
        GameIconKind.SHUFFLE -> {
            drawLine(c, p(3f, 6f, u), p(8f, 6f, u), 2.4f * u, StrokeCap.Round)
            drawLine(c, p(8f, 6f, u), p(16f, 18f, u), 2.4f * u, StrokeCap.Round)
            drawLine(c, p(16f, 18f, u), p(19f, 18f, u), 2.4f * u, StrokeCap.Round)
            drawLine(c, p(3f, 18f, u), p(8f, 18f, u), 2.4f * u, StrokeCap.Round)
            drawLine(c, p(8f, 18f, u), p(16f, 6f, u), 2.4f * u, StrokeCap.Round)
            drawLine(c, p(16f, 6f, u), p(19f, 6f, u), 2.4f * u, StrokeCap.Round)
            drawPath(Path().apply { moveTo(18.5f * u, 2.5f * u); lineTo(22.5f * u, 6f * u); lineTo(18.5f * u, 9.5f * u); close() }, c)
            drawPath(Path().apply { moveTo(18.5f * u, 14.5f * u); lineTo(22.5f * u, 18f * u); lineTo(18.5f * u, 21.5f * u); close() }, c)
        }
        GameIconKind.WHEEL -> {
            drawCircle(c, 11f * u, p(12f, 12f, u))
            for (i in 0 until 8 step 2) {
                drawArc(Color(0xFF0B2383), -90f + i * 45f, 45f, true, p(3f, 3f, u), Size(18f * u, 18f * u))
            }
            drawCircle(c, 2.6f * u, p(12f, 12f, u))
        }
        GameIconKind.KEYBOARD -> {
            drawRoundRect(c, p(1.5f, 5f, u), Size(21f * u, 14f * u), CornerRadius(2.5f * u), style = Stroke(2f * u))
            for (row in 0..1) for (col in 0..4) {
                drawRect(c, p(4f + col * 3.4f, 8f + row * 3.4f, u), Size(2f * u, 2f * u))
            }
            drawRect(c, p(7f, 15f, u), Size(10f * u, 1.8f * u))
        }
        GameIconKind.BULB -> {
            drawCircle(c, 6.5f * u, p(12f, 10f, u))
            drawRect(c, p(9f, 15f, u), Size(6f * u, 4f * u))
            drawRect(c, p(9.8f, 20f, u), Size(4.4f * u, 1.6f * u))
            for (a in listOf(-150.0, -90.0, -30.0, 180.0, 0.0)) {
                val r = Math.toRadians(a)
                drawLine(c, p(12f + 8.5f * cos(r).toFloat(), 10f + 8.5f * sin(r).toFloat(), u),
                    p(12f + 10.8f * cos(r).toFloat(), 10f + 10.8f * sin(r).toFloat(), u), 1.8f * u, StrokeCap.Round)
            }
        }
        GameIconKind.MIC -> {
            drawRoundRect(c, p(8.5f, 2f, u), Size(7f * u, 12f * u), CornerRadius(3.5f * u))
            drawArc(c, 0f, 180f, false, p(5f, 6f, u), Size(14f * u, 12f * u), style = Stroke(2f * u))
            drawLine(c, p(12f, 18f, u), p(12f, 22f, u), 2f * u)
        }
        GameIconKind.MUSIC -> {
            drawCircle(c, 3.2f * u, p(7f, 18f, u)); drawCircle(c, 3.2f * u, p(17f, 16f, u))
            drawLine(c, p(10f, 18f, u), p(10f, 5f, u), 2f * u)
            drawLine(c, p(20f, 16f, u), p(20f, 3f, u), 2f * u)
            drawLine(c, p(10f, 5f, u), p(20f, 3f, u), 3f * u)
        }
        GameIconKind.SPEAKER -> {
            drawPath(Path().apply {
                moveTo(3f * u, 9f * u); lineTo(8f * u, 9f * u); lineTo(13f * u, 4f * u)
                lineTo(13f * u, 20f * u); lineTo(8f * u, 15f * u); lineTo(3f * u, 15f * u); close()
            }, c)
            drawArc(c, -45f, 90f, false, p(10f, 7f, u), Size(10f * u, 10f * u), style = Stroke(2f * u))
            drawArc(c, -45f, 90f, false, p(8f, 3.5f, u), Size(16f * u, 17f * u), style = Stroke(2f * u))
        }
        GameIconKind.BELL -> {
            drawArc(c, 180f, 180f, true, p(5f, 3.5f, u), Size(14f * u, 14f * u))
            drawRect(c, p(5f, 10.5f, u), Size(14f * u, 6f * u))
            drawRoundRect(c, p(3f, 16f, u), Size(18f * u, 2.5f * u), CornerRadius(1f * u))
            drawCircle(c, 2f * u, p(12f, 20.5f, u))
        }
        GameIconKind.LIST -> for (i in 0..2) {
            drawCircle(c, 1.6f * u, p(4.5f, 6f + i * 6f, u))
            drawLine(c, p(9f, 6f + i * 6f, u), p(21f, 6f + i * 6f, u), 2.4f * u, StrokeCap.Round)
        }
        GameIconKind.COINS -> for (i in 0..2) {
            drawOval(c, p(4f + i * 1.5f, 15f - i * 5f, u), Size(15f * u, 6f * u))
            drawOval(Color(0x55000000), p(4f + i * 1.5f, 15f - i * 5f, u), Size(15f * u, 6f * u), style = Stroke(1.2f * u))
        }
        GameIconKind.TIMER -> {
            drawCircle(c, 9f * u, p(12f, 13.5f, u), style = line)
            drawLine(c, p(12f, 13.5f, u), p(12f, 8.5f, u), 2.2f * u, StrokeCap.Round)
            drawLine(c, p(12f, 13.5f, u), p(15.5f, 15.5f, u), 2.2f * u, StrokeCap.Round)
            drawLine(c, p(9.5f, 2f, u), p(14.5f, 2f, u), 2.4f * u, StrokeCap.Round)
        }
        GameIconKind.PERSON -> {
            drawCircle(c, 5f * u, p(12f, 8f, u))
            drawArc(c, 180f, 180f, true, p(3.5f, 14f, u), Size(17f * u, 14f * u))
        }
        GameIconKind.STAR -> drawPath(Path().apply {
            for (i in 0 until 10) {
                val a = Math.toRadians(-90.0 + i * 36.0)
                val r = if (i % 2 == 0) 11f else 4.8f
                val x = (12f + r * cos(a).toFloat()) * u
                val y = (12.5f + r * sin(a).toFloat()) * u
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }, c)
    }
}
