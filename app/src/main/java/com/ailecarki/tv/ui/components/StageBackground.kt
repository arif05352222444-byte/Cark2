package com.ailecarki.tv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import kotlin.random.Random

/**
 * Yarışma stüdyosu arka planı: spotlar, ışık hüzmeleri, İstanbul silueti, altın neon çerçeveler,
 * parlak sahne zemini. Tamamen statik → bir kez çizilir, TV Box'ı yormaz. Blur yok, sadece gradyan.
 */
@Composable
fun StageBackground(modifier: Modifier = Modifier, showCity: Boolean = true) {
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        // Taban: gece mavisi → mor atmosfer
        drawRect(
            Brush.verticalGradient(
                0f to Color(0xFF050A2E),
                0.35f to Color(0xFF0B1E78),
                0.62f to Color(0xFF1A2A9A),
                0.72f to Color(0xFF2A1C78),
                1f to Color(0xFF050822),
            ),
        )
        // Merkezde mor/mavi hale
        drawCircle(
            Brush.radialGradient(listOf(Color(0x664E5BFF), Color(0x00000000)), Offset(w * 0.5f, h * 0.45f), w * 0.45f),
            radius = w * 0.45f, center = Offset(w * 0.5f, h * 0.45f),
        )
        drawBeams(w, h)
        if (showCity) drawCity(w, h)
        drawSideFrames(w, h)
        drawFloor(w, h)
        drawTopLights(w, h)
    }
}

private fun DrawScope.drawBeams(w: Float, h: Float) {
    // Üstten mavi hüzmeler
    val blue = listOf(0.08f, 0.22f, 0.36f, 0.64f, 0.78f, 0.92f)
    blue.forEachIndexed { i, x ->
        val dir = if (x < 0.5f) 1f else -1f
        val path = Path().apply {
            moveTo(w * x - w * 0.008f, 0f)
            lineTo(w * x + w * 0.008f, 0f)
            lineTo(w * (x + dir * 0.12f) + w * 0.07f, h * 0.78f)
            lineTo(w * (x + dir * 0.12f) - w * 0.07f, h * 0.78f)
            close()
        }
        val a = if (i % 2 == 0) 0x30 else 0x22
        drawPath(path, Brush.verticalGradient(listOf(Color(a shl 24 or 0x9FC2FF), Color.Transparent), 0f, h * 0.78f))
    }
    // Yanlardan sıcak sarı spotlar
    listOf(0.02f to 1f, 0.98f to -1f).forEach { (x, dir) ->
        val path = Path().apply {
            moveTo(w * x, h * 0.05f)
            lineTo(w * (x + dir * 0.28f), h * 0.55f)
            lineTo(w * (x + dir * 0.16f), h * 0.62f)
            close()
        }
        drawPath(path, Brush.linearGradient(listOf(Color(0x40FFC24A), Color.Transparent), Offset(w * x, h * 0.05f), Offset(w * (x + dir * 0.24f), h * 0.6f)))
    }
}

private fun DrawScope.drawCity(w: Float, h: Float) {
    val water = h * 0.6f
    // Ufuk ışıltısı (mor-turuncu)
    drawRect(
        Brush.verticalGradient(listOf(Color.Transparent, Color(0x33FF7A59), Color(0x22B06CFF)), h * 0.38f, water),
        Offset(w * 0.2f, h * 0.38f), Size(w * 0.6f, water - h * 0.38f),
    )
    val sil = Color(0xCC0A1040)
    // Köprü
    val deckY = water - h * 0.055f
    val t1 = w * 0.3f
    val t2 = w * 0.44f
    drawRect(sil, Offset(t1 - w * 0.004f, water - h * 0.16f), Size(w * 0.008f, h * 0.16f))
    drawRect(sil, Offset(t2 - w * 0.004f, water - h * 0.16f), Size(w * 0.008f, h * 0.16f))
    drawRect(sil, Offset(w * 0.2f, deckY), Size(w * 0.3f, h * 0.008f))
    val cable = Path().apply {
        moveTo(w * 0.2f, deckY)
        quadraticBezierTo((w * 0.2f + t1) / 2f, deckY - h * 0.02f, t1, water - h * 0.16f)
        quadraticBezierTo((t1 + t2) / 2f, deckY + h * 0.01f, t2, water - h * 0.16f)
        quadraticBezierTo((t2 + w * 0.5f) / 2f, deckY - h * 0.02f, w * 0.5f, deckY)
    }
    drawPath(cable, sil, style = Stroke(h * 0.004f))
    for (i in 0..24) {
        val x = w * 0.2f + i * w * 0.0125f
        drawCircle(Color(0xAAFFD27A), h * 0.0022f, Offset(x, deckY - h * 0.002f))
    }
    // Tarihi yarımada: kubbeler + minareler
    val baseX = w * 0.56f
    drawRect(sil, Offset(w * 0.5f, water - h * 0.05f), Size(w * 0.32f, h * 0.05f))
    val domes = listOf(0.60f to 0.06f, 0.66f to 0.085f, 0.72f to 0.055f, 0.77f to 0.04f)
    domes.forEach { (x, r) ->
        drawArc(sil, 180f, 180f, true, Offset(w * x - h * r, water - h * 0.05f - h * r), Size(h * r * 2, h * r * 2))
    }
    listOf(0.585f, 0.63f, 0.69f, 0.745f).forEach { x ->
        drawRect(sil, Offset(w * x, water - h * 0.24f), Size(w * 0.004f, h * 0.2f))
        drawPath(Path().apply {
            moveTo(w * x - w * 0.001f, water - h * 0.24f)
            lineTo(w * x + w * 0.002f, water - h * 0.27f)
            lineTo(w * x + w * 0.005f, water - h * 0.24f)
            close()
        }, sil)
    }
    // Galata benzeri kule
    drawRect(sil, Offset(baseX - w * 0.035f, water - h * 0.17f), Size(w * 0.016f, h * 0.17f))
    drawPath(Path().apply {
        moveTo(baseX - w * 0.037f, water - h * 0.17f)
        lineTo(baseX - w * 0.027f, water - h * 0.22f)
        lineTo(baseX - w * 0.017f, water - h * 0.17f)
        close()
    }, sil)
    // Pencere ışıkları
    val rnd = Random(7)
    repeat(70) {
        val x = w * (0.5f + rnd.nextFloat() * 0.32f)
        val y = water - h * (0.005f + rnd.nextFloat() * 0.04f)
        drawCircle(Color(0x99FFD98A), h * 0.0022f, Offset(x, y))
    }
    // Su ve yansımalar
    drawRect(
        Brush.verticalGradient(listOf(Color(0x552B45C8), Color(0x00000000)), water, h * 0.7f),
        Offset(w * 0.18f, water), Size(w * 0.66f, h * 0.1f),
    )
    repeat(40) {
        val x = w * (0.2f + rnd.nextFloat() * 0.6f)
        val y = water + h * (0.008f + rnd.nextFloat() * 0.08f)
        drawRect(Color(0x44FFD98A), Offset(x, y), Size(w * (0.01f + rnd.nextFloat() * 0.02f), h * 0.003f))
    }
}

private fun DrawScope.drawSideFrames(w: Float, h: Float) {
    val gold = Color(0xCCFFB53A)
    val frameW = w * 0.03f
    listOf(0f, w - frameW).forEach { x ->
        drawRect(Brush.verticalGradient(listOf(Color(0xFF0A1450), Color(0xFF060B30))), Offset(x, h * 0.12f), Size(frameW, h * 0.6f))
        val inset = if (x == 0f) frameW * 0.55f else frameW * 0.2f
        drawRoundRect(gold, Offset(x + inset, h * 0.15f), Size(frameW * 0.25f, h * 0.52f), CornerRadius(4f), style = Stroke(h * 0.004f))
        drawRoundRect(Color(0x33FFC24A), Offset(x + inset - 6f, h * 0.15f - 6f), Size(frameW * 0.25f + 12f, h * 0.52f + 12f), CornerRadius(8f), style = Stroke(h * 0.008f))
    }
}

private fun DrawScope.drawFloor(w: Float, h: Float) {
    val top = h * 0.68f
    drawRect(
        Brush.verticalGradient(
            0f to Color(0xFF1B2F9E),
            0.35f to Color(0xFF101C66),
            1f to Color(0xFF050822),
            startY = top, endY = h,
        ),
        Offset(0f, top), Size(w, h - top),
    )
    // Sahne kenar ışık çizgileri
    drawLine(Color(0xAAFFC24A), Offset(0f, top), Offset(w, top), h * 0.004f)
    drawLine(Color(0x665AA8FF), Offset(0f, top + h * 0.012f), Offset(w, top + h * 0.012f), h * 0.002f)
    for (i in 0..48) {
        val x = w * i / 48f
        drawCircle(Color(0xCCFFD66B), h * 0.0035f, Offset(x, top - h * 0.012f))
    }
    // Zemin yansımaları (dikey parlak şeritler)
    val rnd = Random(3)
    repeat(26) {
        val x = w * rnd.nextFloat()
        val col = if (it % 3 == 0) Color(0x33FFC24A) else Color(0x264F8BFF)
        drawRect(
            Brush.verticalGradient(listOf(col, Color.Transparent), top, top + h * 0.2f),
            Offset(x, top), Size(w * 0.004f, h * 0.2f),
        )
    }
    // Alt kavisli altın sahne çizgisi
    val arc = Path().apply {
        moveTo(0f, h * 0.9f)
        quadraticBezierTo(w * 0.5f, h * 0.84f, w, h * 0.9f)
    }
    drawPath(arc, Color(0x88FFB53A), style = Stroke(h * 0.004f))
    drawPath(arc, Color(0x22FFB53A), style = Stroke(h * 0.016f))
}

private fun DrawScope.drawTopLights(w: Float, h: Float) {
    for (i in 0..11) {
        val cx = w * (0.04f + i * 0.084f)
        val r = h * 0.045f
        drawCircle(
            Brush.radialGradient(listOf(Color(0xCCFFF3C8), Color(0x44FFD27A), Color.Transparent), Offset(cx, h * 0.01f), r),
            radius = r, center = Offset(cx, h * 0.01f),
        )
    }
}
