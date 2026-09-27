package com.ailecarki.tv.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont

/**
 * Final bilgi şeridi (TV'den uzaktan okunacak şekilde):
 *  VERİLEN HARFLER (R S T L N E)  |  SENİN HARFLERİN (? ? ? + ?)
 * Harfler, harf kartelasındaki gibi altın kutucuklar. Boş seçim yuvaları kesik çizgili "?".
 */
@Composable
fun FinalLettersBar(
    given: List<Char>,
    consonants: List<Char>,
    vowels: List<Char>,
    consonantSlots: Int,
    vowelSlots: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .neonPanel(16.dp, glow = true, borderColor = AppColors.Gold)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.final_given_label), color = AppColors.GoldLight, fontSize = 14.sp, fontFamily = GameFont)
            Spacer(Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                given.forEach { MiniTile(it, filled = true) }
            }
        }
        // Ayraç
        Box(Modifier.width(2.dp).height(48.dp).drawBehind { drawRect(AppColors.Gold.copy(alpha = 0.5f)) })
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(stringResource(R.string.final_picks_label), color = AppColors.GoldLight, fontSize = 14.sp, fontFamily = GameFont)
            Spacer(Modifier.height(3.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                for (i in 0 until consonantSlots) {
                    val c = consonants.getOrNull(i)
                    MiniTile(c, filled = c != null)
                }
                Text("+", color = Color.White, fontSize = 22.sp, fontFamily = GameFont, modifier = Modifier.padding(horizontal = 4.dp))
                for (i in 0 until vowelSlots) {
                    val c = vowels.getOrNull(i)
                    MiniTile(c, filled = c != null)
                }
            }
        }
    }
}

/** Harf kartelası tuşuna benzeyen küçük kutu. Dolu: altın zemin + lacivert harf. Boş: kesik çizgili çerçeve + "?". */
@Composable
private fun MiniTile(letter: Char?, filled: Boolean, tile: Dp = 36.dp) {
    Box(
        Modifier
            .size(tile)
            .drawBehind {
                val r = CornerRadius(8.dp.toPx())
                if (filled) {
                    drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFFFEA8A), Color(0xFFFFC928), Color(0xFFF2A200))), cornerRadius = r)
                    drawRoundRect(Color(0x55FFFFFF), size = size.copy(height = size.height * 0.45f), cornerRadius = r)
                    drawRoundRect(Color(0xFFFFF3B0), cornerRadius = r, style = Stroke(1.6.dp.toPx()))
                } else {
                    drawRoundRect(Color(0xFF081050), cornerRadius = r)
                    drawRoundRect(
                        AppColors.Gold.copy(alpha = 0.85f), cornerRadius = r,
                        style = Stroke(1.8.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f * density, 4f * density))),
                    )
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            letter?.toString() ?: "?",
            color = if (filled) AppColors.Navy else AppColors.GoldLight.copy(alpha = 0.8f),
            fontSize = 20.sp,
            fontFamily = GameFont,
        )
    }
}
