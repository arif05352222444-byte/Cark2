package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.audio.SoundId
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont

enum class PanelState { NORMAL, FOCUSED, DISABLED, PRIMARY }

/**
 * Yarışma paneli çizimi: koyu mavi gradyan + üst parlaklık (gloss) + ince neon kenar.
 * Odakta altın gradyan + sıcak hale. Tüm butonlar, satırlar ve kartlar bunu kullanır.
 */
fun DrawScope.drawGamePanel(state: PanelState, corner: Float, glowAlpha: Float = 1f) {
    val r = CornerRadius(corner)
    val focused = state == PanelState.FOCUSED
    if (focused) {
        for (i in 4 downTo 1) {
            val g = i * 3.5f * density
            drawRoundRect(
                AppColors.Gold.copy(alpha = (0.09f + 0.05f * (4 - i)) * glowAlpha),
                Offset(-g, -g), Size(size.width + g * 2, size.height + g * 2), CornerRadius(corner + g),
            )
        }
    }
    val body = when (state) {
        PanelState.FOCUSED, PanelState.PRIMARY -> Brush.verticalGradient(
            listOf(Color(0xFFFFEA8A), Color(0xFFFFC928), Color(0xFFF2A200)),
        )
        PanelState.DISABLED -> Brush.verticalGradient(listOf(Color(0xFF1A2560), Color(0xFF0F1640)))
        PanelState.NORMAL -> Brush.verticalGradient(listOf(AppColors.ButtonTop, AppColors.ButtonBottom))
    }
    drawRoundRect(body, cornerRadius = r)
    // Gloss: üst yarıda yumuşak beyaz parlaklık
    drawRoundRect(
        Brush.verticalGradient(listOf(Color(0x40FFFFFF), Color(0x08FFFFFF), Color.Transparent), 0f, size.height * 0.55f),
        size = Size(size.width, size.height * 0.55f),
        cornerRadius = r,
    )
    val border = when (state) {
        PanelState.FOCUSED -> Color(0xFFFFF3B0)
        PanelState.PRIMARY -> Color(0xFFFFE070)
        PanelState.DISABLED -> Color(0x553A5AB0)
        PanelState.NORMAL -> AppColors.Neon
    }
    val bw = if (focused) 3.dp.toPx() else 1.6.dp.toPx()
    if (state == PanelState.NORMAL) {
        drawRoundRect(AppColors.Neon.copy(alpha = 0.25f), cornerRadius = r, style = Stroke(bw * 3.5f))
    }
    drawRoundRect(border, cornerRadius = r, style = Stroke(bw))
}

/**
 * Kumanda butonu: normal / focused / pressed / disabled.
 * Disabled buton odaklanabilir kalır (odak kaybolmasın) ama OK çalışmaz.
 */
@Composable
fun TvButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    primary: Boolean = false,
    focusRequester: FocusRequester? = null,
    height: Dp = 60.dp,
    fontSize: TextUnit = 22.sp,
    corner: Dp = 16.dp,
    icon: GameIconKind? = null,
    alignStart: Boolean = false,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val pressed by interaction.collectIsPressedAsState()
    val sound = LocalSoundPlayer.current
    val scale by animateFloatAsState(
        targetValue = when {
            pressed && enabled -> 0.96f
            focused -> 1.06f
            else -> 1f
        },
        label = "tvButtonScale",
    )
    LaunchedEffect(focused) { if (focused) sound(SoundId.UI_MOVE) }

    val state = when {
        focused && enabled -> PanelState.FOCUSED
        focused -> PanelState.FOCUSED
        !enabled -> PanelState.DISABLED
        primary -> PanelState.PRIMARY
        else -> PanelState.NORMAL
    }
    val gold = state == PanelState.FOCUSED || state == PanelState.PRIMARY
    val contentColor = when {
        gold -> AppColors.Navy
        !enabled -> Color(0xFF5C6A9E)
        else -> Color.White
    }
    val contentAlpha = if (!enabled && focused) 0.55f else 1f

    Row(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; alpha = if (!enabled && !focused) 0.7f else 1f }
            .height(height)
            .drawBehind { drawGamePanel(state, corner.toPx(), if (enabled) 1f else 0.4f) }
            .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier)
            .clickable(interactionSource = interaction, indication = null) {
                if (enabled) {
                    sound(SoundId.UI_SELECT)
                    onClick()
                } else {
                    sound(SoundId.UI_DISABLED)
                }
            }
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (alignStart) Arrangement.Start else Arrangement.Center,
    ) {
        if (icon != null) {
            GameIcon(icon, contentColor.copy(alpha = contentAlpha), size = (height.value * 0.46f).dp)
            Spacer(Modifier.width(14.dp))
        }
        Text(
            text = text,
            color = contentColor.copy(alpha = contentAlpha),
            fontSize = fontSize,
            fontFamily = GameFont,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}
