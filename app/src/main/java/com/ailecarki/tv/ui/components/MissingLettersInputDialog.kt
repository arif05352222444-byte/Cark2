package com.ailecarki.tv.ui.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.engine.PartialAnswerComposer
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont

/**
 * "Yazarak gir" yedeği: açık harfler hazır gelir, oyuncu yalnızca eksik kutuları doldurur.
 */
@Composable
fun MissingLettersInputDialog(
    title: String,
    answer: String,
    revealed: Set<Char>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    subtitle: String? = null,
) {
    val missingPositions = remember(answer, revealed) { PartialAnswerComposer.missingPositions(answer, revealed) }
    var entered by remember(answer, revealed) { mutableStateOf(emptyList<Char>()) }
    val missingIndexByPosition = remember(missingPositions) {
        missingPositions.withIndex().associate { (entryIndex, answerIndex) -> answerIndex to entryIndex }
    }
    val nextPosition = missingPositions.getOrNull(entered.size)

    fun add(c: Char) {
        if (entered.size < missingPositions.size) entered = entered + c
    }

    GameDialog(onDismiss = onDismiss, maxWidth = 980.dp) {
        Text(title, style = goldTextStyle(30.sp))
        if (subtitle != null) {
            Text(subtitle, color = AppColors.GoldLight, fontSize = 21.sp, fontWeight = FontWeight.Black)
        }
        Text(
            stringResource(R.string.input_missing_hint),
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            layoutLines(answer).forEach { line ->
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    line.forEachIndexed { wordIndex, word ->
                        if (wordIndex > 0) Spacer(Modifier.width(14.dp))
                        word.text.forEachIndexed { charOffset, original ->
                            val answerIndex = word.startIndex + charOffset
                            val missingEntryIndex = missingIndexByPosition[answerIndex]
                            val fixed = missingEntryIndex == null
                            val typed = missingEntryIndex?.let { entered.getOrNull(it) }
                            val isNext = answerIndex == nextPosition
                            MissingTile(
                                text = when {
                                    fixed -> original.toString()
                                    typed != null -> typed.toString()
                                    else -> ""
                                },
                                fixed = fixed,
                                active = isNext,
                            )
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                }
            }
        }

        LetterKeyboard(
            isEnabled = { entered.size < missingPositions.size },
            onLetter = ::add,
            keySize = 42.dp,
            focusLetter = 'A',
        )

        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            TvButton(
                stringResource(R.string.input_delete),
                onClick = { if (entered.isNotEmpty()) entered = entered.dropLast(1) },
                enabled = entered.isNotEmpty(),
                height = 52.dp,
                fontSize = 18.sp,
            )
            TvButton(
                stringResource(R.string.input_clear),
                onClick = { entered = emptyList() },
                enabled = entered.isNotEmpty(),
                height = 52.dp,
                fontSize = 18.sp,
            )
            TvButton(
                stringResource(R.string.input_ok),
                onClick = { onConfirm(PartialAnswerComposer.compose(answer, revealed, entered)) },
                enabled = entered.size == missingPositions.size,
                primary = true,
                height = 52.dp,
                fontSize = 20.sp,
            )
        }
    }
}

@Composable
private fun MissingTile(text: String, fixed: Boolean, active: Boolean) {
    val shape = RoundedCornerShape(9.dp)
    val border = when {
        active -> AppColors.Gold
        fixed -> AppColors.BlueLight
        else -> Color(0xFF5D70B2)
    }
    val bg = when {
        fixed -> AppColors.TileFace
        text.isNotEmpty() -> Color(0xFFFFF0B0)
        else -> Color(0xFFD7DEEE)
    }
    Box(
        Modifier
            .size(width = 42.dp, height = 50.dp)
            .background(bg, shape)
            .border(if (active) 4.dp else 2.dp, border, shape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (text.isEmpty()) "·" else text,
            color = if (text.isEmpty()) Color(0xFF8792B0) else AppColors.Navy,
            fontSize = 25.sp,
            fontFamily = GameFont,
            fontWeight = FontWeight.Black,
        )
    }
}
