package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.audio.SoundId
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont

/** TV kumandasıyla seçilebilen, altın mühürlü sürpriz zarf. */
@Composable
fun PrizeEnvelopeCard(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val pressed by interaction.collectIsPressedAsState()
    val sound = LocalSoundPlayer.current
    val scale by animateFloatAsState(
        targetValue = when {
            pressed -> 0.96f
            focused -> 1.08f
            else -> 1f
        },
        label = "envelopeScale",
    )
    LaunchedEffect(focused) { if (focused) sound(SoundId.UI_MOVE) }

    Box(
        modifier
            .width(230.dp)
            .height(150.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .clickable(interactionSource = interaction, indication = null) {
                sound(SoundId.UI_SELECT)
                onClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val glow = if (focused) 1f else 0.35f
            val grow = 7.dp.toPx()
            if (focused) {
                drawRoundRect(
                    AppColors.Gold.copy(alpha = 0.20f),
                    Offset(-grow, -grow),
                    Size(size.width + grow * 2, size.height + grow * 2),
                )
            }
            val body = Brush.verticalGradient(listOf(Color(0xFFFFE79A), Color(0xFFE8B948), Color(0xFFC98720)))
            drawRoundRect(body, cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()))
            drawRoundRect(
                if (focused) Color(0xFFFFF1A8) else Color(0xFFD89A2B),
                style = Stroke(if (focused) 4.dp.toPx() else 2.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(18.dp.toPx()),
            )
            val flap = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width / 2f, size.height * 0.57f)
                lineTo(size.width, 0f)
                close()
            }
            drawPath(flap, Brush.verticalGradient(listOf(Color(0xFFFFF0B5), Color(0xFFE5B64C))))
            drawLine(Color(0xFFB97A15), Offset(0f, size.height), Offset(size.width / 2f, size.height * 0.52f), 2.dp.toPx())
            drawLine(Color(0xFFB97A15), Offset(size.width, size.height), Offset(size.width / 2f, size.height * 0.52f), 2.dp.toPx())
            val sealCenter = Offset(size.width / 2f, size.height * 0.57f)
            drawCircle(Color(0xFFB2192F), 23.dp.toPx(), sealCenter)
            drawCircle(Color(0xFFFFD86A).copy(alpha = 0.75f * glow), 17.dp.toPx(), sealCenter, style = Stroke(3.dp.toPx()))
        }
        Text(
            "ZARF $number",
            color = AppColors.Navy,
            fontFamily = GameFont,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
        )
    }
}
