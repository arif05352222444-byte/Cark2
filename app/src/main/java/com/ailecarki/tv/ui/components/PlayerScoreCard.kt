package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.model.Player
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import com.ailecarki.tv.ui.theme.formatScore

/**
 * Parlak oyuncu kartı. Puan artınca kısa "pulse", puan düşünce (iflas) kısa "shake".
 * Aktif oyuncu: daha parlak + altın kenar + hale. [doubleBadge] aktif 2X hakkını gösterir.
 */
@Composable
fun PlayerScoreCard(
    player: Player,
    index: Int,
    active: Boolean,
    modifier: Modifier = Modifier,
    doubleBadge: Boolean = false,
    height: Dp = 64.dp,
) {
    val base = AppColors.PlayerColors[index % AppColors.PlayerColors.size]
    val shownScore by animateIntAsState(player.score, tween(800), label = "score")
    val activeAnim by animateFloatAsState(if (active) 1f else 0f, tween(450), label = "active")
    val pulse = remember { Animatable(1f) }
    val shake = remember { Animatable(0f) }
    var lastScore by remember { mutableIntStateOf(player.score) }
    LaunchedEffect(player.score) {
        val old = lastScore
        lastScore = player.score
        if (player.score > old) {
            pulse.animateTo(1.12f, tween(160)); pulse.animateTo(1f, tween(260))
        } else if (player.score < old) {
            for (x in listOf(-10f, 9f, -7f, 5f, -3f, 0f)) shake.animateTo(x, tween(50))
        }
    }
    val corner = 16.dp
    Box(
        modifier
            .graphicsLayer {
                val s = pulse.value * (1f + 0.05f * activeAnim)
                scaleX = s; scaleY = s
                translationX = shake.value * density
            }
            .height(height)
            .drawBehind {
                val r = CornerRadius(corner.toPx())
                if (activeAnim > 0f) {
                    for (i in 4 downTo 1) {
                        val g = i * 3.5f * density
                        drawRoundRect(
                            AppColors.Gold.copy(alpha = (0.08f + 0.05f * (4 - i)) * activeAnim),
                            Offset(-g, -g), Size(size.width + g * 2, size.height + g * 2), CornerRadius(r.x + g),
                        )
                    }
                }
                val top = lerp(base, Color.White, 0.18f + 0.12f * activeAnim)
                val bottom = lerp(base, Color.Black, 0.45f - 0.15f * activeAnim)
                drawRoundRect(Brush.verticalGradient(listOf(top, base, bottom)), cornerRadius = r)
                drawRoundRect(
                    Brush.verticalGradient(listOf(Color(0x55FFFFFF), Color.Transparent), 0f, size.height * 0.5f),
                    size = Size(size.width, size.height * 0.5f), cornerRadius = r,
                )
                drawRoundRect(lerp(base, Color.White, 0.5f).copy(alpha = 0.35f), cornerRadius = r, style = Stroke(5.dp.toPx()))
                drawRoundRect(
                    lerp(lerp(base, Color.White, 0.55f), AppColors.Gold, activeAnim),
                    cornerRadius = r, style = Stroke((1.8f + 1.8f * activeAnim) * density),
                )
            }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(height * 0.56f)
                    .background(Color(0x33000000), CircleShape)
                    .border(2.dp, Color(0xCCFFFFFF), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                GameIcon(GameIconKind.PERSON, Color.White, size = height * 0.34f)
            }
            Spacer(Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    player.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                )
                Text(
                    formatScore(shownScore),
                    color = if (active) AppColors.GoldLight else Color.White,
                    fontSize = (height.value * 0.38f).sp,
                    fontFamily = GameFont,
                )
            }
        }
        if (doubleBadge) {
            Text(
                "2X",
                color = Color.White,
                fontSize = 13.sp,
                fontFamily = GameFont,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp)
                    .background(AppColors.Fuchsia, RoundedCornerShape(8.dp))
                    .border(1.5.dp, Color(0xFFFFB8F0), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 1.dp),
            )
        }
    }
}

/** Referanstaki "SIRA SENDE" yuvarlak altın göstergesi. */
@Composable
fun TurnBadge(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(size)
            .drawBehind {
                val r = this.size.minDimension / 2f
                drawCircle(AppColors.Gold.copy(alpha = 0.25f), r * 1.12f)
                drawCircle(Brush.verticalGradient(listOf(Color(0xFF14257A), Color(0xFF060C3A))), r)
                drawCircle(Brush.verticalGradient(listOf(Color(0xFFFFE58A), Color(0xFFE08A00))), r, style = Stroke(r * 0.12f))
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            stringResource(R.string.your_turn_two_lines),
            color = Color.White,
            fontFamily = GameFont,
            fontSize = (size.value * 0.17f).sp,
            textAlign = TextAlign.Center,
            lineHeight = (size.value * 0.2f).sp,
        )
    }
}

@Composable
fun PlayerScoreRow(
    players: List<Player>,
    activeIndex: Int?,
    modifier: Modifier = Modifier,
    doubleActive: Boolean = false,
    showTurnBadge: Boolean = false,
    height: Dp = 64.dp,
) {
    Row(
        modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showTurnBadge) {
            TurnBadge(height * 1.15f)
            GameIcon(GameIconKind.PLAY, AppColors.Gold, size = 18.dp)
        }
        players.forEachIndexed { i, p ->
            PlayerScoreCard(
                p, i, i == activeIndex,
                Modifier.weight(1f).widthIn(max = 230.dp),
                doubleBadge = doubleActive && i == activeIndex,
                height = height,
            )
        }
    }
}
