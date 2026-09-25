package com.ailecarki.tv.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.ailecarki.tv.ui.theme.AppColors

/** Cam efektli koyu mavi panel + neon kontur (+ isteğe bağlı dış hale). */
fun Modifier.neonPanel(corner: Dp, glow: Boolean = false, borderColor: Color = AppColors.Neon): Modifier = drawBehind {
    val r = CornerRadius(corner.toPx())
    if (glow) {
        for (i in 3 downTo 1) {
            val g = i * 4f * density
            drawRoundRect(
                borderColor.copy(alpha = 0.07f * (4 - i)),
                Offset(-g, -g), Size(size.width + g * 2, size.height + g * 2), CornerRadius(r.x + g),
            )
        }
    }
    drawRoundRect(Brush.verticalGradient(listOf(Color(0xF0142A8C), Color(0xF00A1455), Color(0xF0070D3C))), cornerRadius = r)
    drawRoundRect(
        Brush.verticalGradient(listOf(Color(0x30FFFFFF), Color.Transparent), 0f, size.height * 0.25f),
        size = Size(size.width, size.height * 0.25f), cornerRadius = r,
    )
    drawRoundRect(borderColor.copy(alpha = 0.3f), cornerRadius = r, style = Stroke(6f * density))
    drawRoundRect(borderColor, cornerRadius = r, style = Stroke(1.8f * density))
}
