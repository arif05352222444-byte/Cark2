package com.ailecarki.tv.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.audio.SoundId
import com.ailecarki.tv.domain.engine.TurkishAlphabet
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont

/** 29 harflik Türk alfabesi, 15 + 14 iki satır. */
val KEYBOARD_ROWS: List<List<Char>> = TurkishAlphabet.LETTERS.let { listOf(it.take(15), it.drop(15)) }

/**
 * Harf kartelası. Devre dışı harfler odaklanabilir (grid gezintisi bozulmasın) ama seçilemez.
 * [focusLetter] ilk odaklanacak harf; [focusKey] değişince odak tekrar verilir.
 */
@Composable
fun LetterKeyboard(
    isEnabled: (Char) -> Boolean,
    onLetter: (Char) -> Unit,
    modifier: Modifier = Modifier,
    keySize: Dp = 46.dp,
    focusLetter: Char? = null,
    focusKey: Any? = Unit,
) {
    val requesters = remember { TurkishAlphabet.LETTERS.associateWith { FocusRequester() } }
    val target = focusLetter ?: TurkishAlphabet.LETTERS.firstOrNull(isEnabled) ?: TurkishAlphabet.LETTERS.first()
    RequestFocus(requesters.getValue(target), focusKey)

    Column(modifier, verticalArrangement = Arrangement.spacedBy(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        KEYBOARD_ROWS.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                row.forEach { c ->
                    LetterKey(c, isEnabled(c), keySize, requesters.getValue(c)) { onLetter(c) }
                }
            }
        }
    }
}

@Composable
private fun LetterKey(letter: Char, enabled: Boolean, keySize: Dp, requester: FocusRequester, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val sound = LocalSoundPlayer.current
    val scale by animateFloatAsState(if (focused) 1.1f else 1f, label = "keyScale")
    LaunchedEffect(focused) { if (focused) sound(SoundId.UI_MOVE) }
    Box(
        Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; alpha = if (enabled || focused) 1f else 0.55f }
            .size(width = keySize * 1.12f, height = keySize)
            .drawBehind {
                val state = when {
                    focused -> PanelState.FOCUSED
                    !enabled -> PanelState.DISABLED
                    else -> PanelState.NORMAL
                }
                drawGamePanel(state, 9.dp.toPx(), if (enabled) 1f else 0.45f)
                if (!enabled) {
                    val p = size.minDimension * 0.24f
                    val c = if (focused) Color(0x99402000) else Color(0x995C6A9E)
                    drawLine(c, Offset(p, p), Offset(size.width - p, size.height - p), 2.5.dp.toPx())
                    drawLine(c, Offset(size.width - p, p), Offset(p, size.height - p), 2.5.dp.toPx())
                }
            }
            .focusRequester(requester)
            .clickable(interactionSource = interaction, indication = null) {
                if (enabled) { sound(SoundId.UI_SELECT); onClick() } else sound(SoundId.UI_DISABLED)
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            letter.toString(),
            color = when {
                focused -> AppColors.Navy
                !enabled -> Color(0xFF6D7AB0)
                else -> Color.White
            },
            fontSize = (keySize.value * 0.5f).sp,
            fontFamily = GameFont,
        )
    }
}
