package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.domain.model.Player
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import com.ailecarki.tv.ui.theme.formatScore

/**
 * Parlak oyuncu kartı. Aktif oyuncu çok net anlaşılır: kart büyür, altın çerçeve nabız gibi yanar,
 * isim büyür ve kartın hemen üstünde "SIRA SENDE" etiketi yanıp söner.
 */
@Composable
fun PlayerScoreCard(
    player: Player,
    index: Int,
    active: Boolean,
    modifier: Modifier = Modifier,
    doubleBadge: Boolean = false,
    jokerBadge: Boolean = false,
    height: Dp = 64.dp,
    showTurnLabel: Boolean = false,
) {
    val base = AppColors.PlayerColors[index % AppColors.PlayerColors.size]
    val shownScore by animateIntAsState(player.score, tween(800), label = "score")
    val activeAnim by animateFloatAsState(if (active) 1f else 0f, tween(350), label = "active")
    val blinkTransition = rememberInfiniteTransition(label = "turnBlink")
    val blink by blinkTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "turnBlinkValue",
    )
    val pulse = remember { Animatable(1f) }
    val shake = remember { Animatable(0f) }
    var lastScore by remember { mutableIntStateOf(player.score) }

    LaunchedEffect(player.score) {
        val old = lastScore
        lastScore = player.score
        if (player.score > old) {
            pulse.animateTo(1.13f, tween(150)); pulse.animateTo(1f, tween(260))
        } else if (player.score < old) {
            for (x in listOf(-10f, 9f, -7f, 5f, -3f, 0f)) shake.animateTo(x, tween(50))
        }
    }

    Box(modifier.height(height), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .graphicsLayer {
                    val s = pulse.value * (1f + 0.095f * activeAnim)
                    scaleX = s
                    scaleY = s
                    translationX = shake.value * density
                }
                .drawBehind {
                    val r = CornerRadius(16.dp.toPx())
                    if (activeAnim > 0f) {
                        val glowPulse = 0.50f + 0.50f * blink
                        for (i in 6 downTo 1) {
                            val g = i * 4.5f * density
                            drawRoundRect(
                                AppColors.Gold.copy(alpha = (0.07f + 0.055f * (6 - i)) * activeAnim * glowPulse),
                                Offset(-g, -g),
                                Size(size.width + g * 2, size.height + g * 2),
                                CornerRadius(r.x + g),
                            )
                        }
                    }
                    val top = lerp(base, Color.White, 0.20f + 0.15f * activeAnim)
                    val bottom = lerp(base, Color.Black, 0.46f - 0.16f * activeAnim)
                    drawRoundRect(Brush.verticalGradient(listOf(top, base, bottom)), cornerRadius = r)
                    drawRoundRect(
                        Brush.verticalGradient(listOf(Color(0x66FFFFFF), Color.Transparent), 0f, size.height * 0.52f),
                        size = Size(size.width, size.height * 0.52f),
                        cornerRadius = r,
                    )
                    if (activeAnim > 0f) {
                        val bw = (6.5f + 4.5f * blink) * density
                        drawRoundRect(lerp(AppColors.Gold, Color.White, 0.22f * blink), cornerRadius = r, style = Stroke(bw))
                        drawRoundRect(Color(0xFF7A4200), cornerRadius = r, style = Stroke(1.2f * density))
                    } else {
                        drawRoundRect(lerp(base, Color.White, 0.55f), cornerRadius = r, style = Stroke(2f * density))
                    }
                }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(height * 0.56f)
                        .background(Color(0x33000000), CircleShape)
                        .border(2.dp, if (active) AppColors.GoldLight else Color(0xCCFFFFFF), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    GameIcon(GameIconKind.PERSON, Color.White, size = height * 0.34f)
                }
                Spacer(Modifier.width(10.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        player.name,
                        color = if (active) Color.White else Color(0xFFF3F6FF),
                        fontSize = if (active) 21.sp else 14.sp,
                        fontFamily = if (active) GameFont else null,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        formatScore(shownScore),
                        color = if (active) AppColors.GoldLight else Color.White,
                        fontSize = (height.value * if (active) 0.40f else 0.36f).sp,
                        fontFamily = GameFont,
                    )
                }
            }
            if (doubleBadge || jokerBadge) {
                Text(
                    if (jokerBadge) "JOKER +1.000" else "2X",
                    color = if (jokerBadge) AppColors.Navy else Color.White,
                    fontSize = if (jokerBadge) 10.sp else 13.sp,
                    fontFamily = GameFont,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp)
                        .background(if (jokerBadge) AppColors.GoldLight else AppColors.Fuchsia, RoundedCornerShape(8.dp))
                        .border(1.5.dp, if (jokerBadge) AppColors.Gold else Color(0xFFFFB8F0), RoundedCornerShape(8.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                )
            }
        }

        if (active && showTurnLabel) {
            Box(
                Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-38).dp)
                    .graphicsLayer {
                        alpha = 0.78f + 0.22f * blink
                        val s = 0.96f + 0.07f * blink
                        scaleX = s
                        scaleY = s
                    }
                    .drawBehind {
                        val r = CornerRadius(size.height / 2f)
                        val g = 8f * density
                        drawRoundRect(AppColors.Gold.copy(alpha = 0.22f + 0.22f * blink), Offset(-g, -g), Size(size.width + 2*g, size.height + 2*g), CornerRadius(r.x + g))
                        drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF1739A0), Color(0xFF071449))), cornerRadius = r)
                        drawRoundRect(AppColors.GoldLight, cornerRadius = r, style = Stroke((2.5f + 1.5f * blink) * density))
                    }
                    .padding(horizontal = 18.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text("SIRA SENDE", color = Color.White, fontFamily = GameFont, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
            Text(
                "▼",
                color = AppColors.Gold,
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.TopCenter).offset(y = (-15).dp),
            )
        }
    }
}

/** Eski yuvarlak gösterge başka ekranlarda gerekirse kullanılabilir. */
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
        Text("SIRA\nSENDE", color = Color.White, fontFamily = GameFont, fontSize = (size.value * 0.17f).sp, textAlign = TextAlign.Center, lineHeight = (size.value * 0.2f).sp)
    }
}

@Composable
fun PlayerScoreRow(
    players: List<Player>,
    activeIndex: Int?,
    modifier: Modifier = Modifier,
    doubleActive: Boolean = false,
    jokerActive: Boolean = false,
    showTurnBadge: Boolean = false,
    height: Dp = 64.dp,
) {
    BoxWithConstraints(modifier) {
        val gap = 14.dp
        val n = players.size.coerceAtLeast(1)
        val cardW = min(240.dp, (maxWidth - gap * (n - 1)) / n)
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            players.forEachIndexed { i, p ->
                PlayerScoreCard(
                    p,
                    i,
                    i == activeIndex,
                    Modifier.width(cardW),
                    doubleBadge = doubleActive && i == activeIndex,
                    jokerBadge = jokerActive && i == activeIndex,
                    height = height,
                    showTurnLabel = showTurnBadge && i == activeIndex,
                )
            }
        }
    }
}
