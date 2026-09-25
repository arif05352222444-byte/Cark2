package com.ailecarki.tv.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont

/** "KATEGORİ: ŞEHİR" kapsülü: koyu lacivert, altın kontur, kategori adı altın. */
@Composable
fun CategoryPill(category: String, modifier: Modifier = Modifier) {
    Row(
        modifier
            .drawBehind {
                val r = CornerRadius(size.height / 2f)
                drawRoundRect(AppColors.Gold.copy(alpha = 0.18f), cornerRadius = r, style = Stroke(7.dp.toPx()))
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF0E1C66), Color(0xFF050A33))), cornerRadius = r)
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color(0xFFFFE58A), Color(0xFFE08A00))),
                    cornerRadius = r, style = Stroke(2.2.dp.toPx()),
                )
            }
            .padding(horizontal = 40.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(stringResource(R.string.category_label), color = Color.White, fontSize = 17.sp, fontFamily = GameFont)
        Spacer(Modifier.width(8.dp))
        Text(category, style = goldTextStyle(21.sp))
    }
}
