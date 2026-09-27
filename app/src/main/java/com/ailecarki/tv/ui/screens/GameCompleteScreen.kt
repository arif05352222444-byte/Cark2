package com.ailecarki.tv.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.audio.SoundId
import com.ailecarki.tv.domain.engine.TurkishAlphabet
import com.ailecarki.tv.domain.model.GameState
import com.ailecarki.tv.ui.components.CelebrationLayer
import com.ailecarki.tv.ui.components.CelebrationLevel
import com.ailecarki.tv.ui.components.GoldText
import com.ailecarki.tv.ui.components.LocalSoundPlayer
import com.ailecarki.tv.ui.components.PlayerScoreCard
import com.ailecarki.tv.ui.components.PrizeEnvelopeCard
import com.ailecarki.tv.ui.components.PuzzleBoard
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.goldTextStyle
import com.ailecarki.tv.ui.components.neonPanel
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.Dimens
import com.ailecarki.tv.ui.theme.GameFont
import com.ailecarki.tv.ui.viewmodel.GameViewModel

private enum class PrizeReward { WISH, PENALTY, MONEY }

@Composable
fun GameCompleteScreen(s: GameState, vm: GameViewModel, onNewGame: () -> Unit, onMenu: () -> Unit) {
    val won = s.finalWon == true
    val championIndex = vm.overallWinner()
    val champion = s.players[championIndex]
    val finalist = s.players[s.finalistIndex ?: championIndex]
    var envelopeStage by rememberSaveable(s.eventCounter) { mutableStateOf(false) }
    var selectedEnvelope by rememberSaveable(s.eventCounter) { mutableStateOf(-1) }
    val rewardOrder = remember(s.eventCounter, champion.name) { PrizeReward.entries.shuffled() }

    Box(Modifier.fillMaxSize()) {
        if (won) {
            CelebrationLayer(
                trigger = if (envelopeStage) "envelopes-${s.eventCounter}-$selectedEnvelope" else "final-${s.eventCounter}-${champion.name}",
                level = CelebrationLevel.FINAL,
            )
        }

        if (won && envelopeStage) {
            PrizeEnvelopeStage(
                s = s,
                championIndex = championIndex,
                selectedEnvelope = selectedEnvelope,
                rewardOrder = rewardOrder,
                onSelect = { selectedEnvelope = it },
                onNewGame = onNewGame,
                onMenu = onMenu,
            )
        } else {
            ChampionSummary(
                s = s,
                won = won,
                championIndex = championIndex,
                finalistName = finalist.name,
                onEnvelopes = { envelopeStage = true },
                onNewGame = onNewGame,
                onMenu = onMenu,
            )
        }
    }
}

@Composable
private fun ChampionSummary(
    s: GameState,
    won: Boolean,
    championIndex: Int,
    finalistName: String,
    onEnvelopes: () -> Unit,
    onNewGame: () -> Unit,
    onMenu: () -> Unit,
) {
    val champion = s.players[championIndex]
    val first = remember { FocusRequester() }
    RequestFocus(first, Unit)

    val pulseTransition = rememberInfiniteTransition(label = "championPulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.045f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
        label = "championScale",
    )

    Column(
        Modifier
            .fillMaxSize()
            .padding(horizontal = Dimens.SafeHorizontal, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (won) {
            GoldText(stringResource(R.string.final_win_title), 54.sp)
            Text(
                stringResource(R.string.final_win_sub, finalistName.uppercase(TurkishAlphabet.LOCALE)),
                color = Color.White,
                fontSize = 32.sp,
                fontFamily = GameFont,
                fontWeight = FontWeight.Black,
            )
        } else {
            Text(stringResource(R.string.final_lose_title), style = goldTextStyle(46.sp))
            Text(stringResource(R.string.final_lose_sub), color = AppColors.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        key(s.puzzle.id) {
            PuzzleBoard(
                s.puzzle.answer,
                s.revealedLetters,
                Modifier.fillMaxWidth().height(if (won) 112.dp else 130.dp),
            )
        }

        if (won) {
            Spacer(Modifier.height(2.dp))
            Box(
                Modifier
                    .graphicsLayer { scaleX = pulse; scaleY = pulse }
                    .neonPanel(20.dp, glow = true)
                    .padding(horizontal = 30.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                GoldText("ŞAMPİYON ${champion.name.uppercase(TurkishAlphabet.LOCALE)}", 48.sp)
            }
            Box(Modifier.padding(top = 4.dp, bottom = 4.dp)) {
                PlayerScoreCard(
                    player = champion,
                    index = championIndex,
                    active = true,
                    modifier = Modifier.width(390.dp),
                    height = 90.dp,
                    showTurnLabel = false,
                )
            }
        } else {
            Text(stringResource(R.string.champion, champion.name), style = goldTextStyle(30.sp), textAlign = TextAlign.Center)
            PlayerScoreCard(
                player = champion,
                index = championIndex,
                active = true,
                modifier = Modifier.width(350.dp),
                height = 82.dp,
                showTurnLabel = false,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.padding(top = 6.dp)) {
            if (won) {
                TvButton(
                    stringResource(R.string.prize_open_envelopes),
                    onEnvelopes,
                    Modifier.width(300.dp),
                    primary = true,
                    focusRequester = first,
                )
                TvButton(stringResource(R.string.menu_new_game), onNewGame, Modifier.width(250.dp))
                TvButton(stringResource(R.string.main_menu), onMenu, Modifier.width(250.dp))
            } else {
                TvButton(stringResource(R.string.menu_new_game), onNewGame, Modifier.width(300.dp), primary = true, focusRequester = first)
                TvButton(stringResource(R.string.main_menu), onMenu, Modifier.width(300.dp))
            }
        }
    }
}

@Composable
private fun PrizeEnvelopeStage(
    s: GameState,
    championIndex: Int,
    selectedEnvelope: Int,
    rewardOrder: List<PrizeReward>,
    onSelect: (Int) -> Unit,
    onNewGame: () -> Unit,
    onMenu: () -> Unit,
) {
    val champion = s.players[championIndex]
    val losers = s.players.filterIndexed { index, _ -> index != championIndex }
    val losersText = losers.joinToString(", ") { it.name.uppercase(TurkishAlphabet.LOCALE) }
    val firstEnvelope = remember { FocusRequester() }
    val firstAction = remember { FocusRequester() }
    val sound = LocalSoundPlayer.current
    RequestFocus(if (selectedEnvelope < 0) firstEnvelope else firstAction, selectedEnvelope)

    Column(
        Modifier.fillMaxSize().padding(horizontal = Dimens.SafeHorizontal, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        GoldText("ŞAMPİYON ${champion.name.uppercase(TurkishAlphabet.LOCALE)}", 44.sp)
        Text(
            if (selectedEnvelope < 0) stringResource(R.string.prize_pick_title) else stringResource(R.string.prize_reveal_title),
            color = Color.White,
            fontSize = 28.sp,
            fontFamily = GameFont,
            fontWeight = FontWeight.Black,
        )

        if (selectedEnvelope < 0) {
            Text(
                stringResource(R.string.prize_pick_subtitle),
                color = AppColors.GoldLight,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(34.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { index ->
                    PrizeEnvelopeCard(
                        number = index + 1,
                        onClick = {
                            sound(SoundId.FX_CELEBRATION)
                            onSelect(index)
                        },
                        focusRequester = if (index == 0) firstEnvelope else null,
                    )
                }
            }
        } else {
            val reward = rewardOrder[selectedEnvelope]
            val title = when (reward) {
                PrizeReward.WISH -> stringResource(R.string.prize_wish_title)
                PrizeReward.PENALTY -> stringResource(R.string.prize_penalty_title)
                PrizeReward.MONEY -> stringResource(R.string.prize_money_title)
            }
            val body = when (reward) {
                PrizeReward.WISH -> stringResource(R.string.prize_wish_body, champion.name.uppercase(TurkishAlphabet.LOCALE), losersText)
                PrizeReward.PENALTY -> stringResource(R.string.prize_penalty_body, champion.name.uppercase(TurkishAlphabet.LOCALE), losersText)
                PrizeReward.MONEY -> stringResource(R.string.prize_money_body, losersText, champion.name.uppercase(TurkishAlphabet.LOCALE))
            }
            Box(
                Modifier
                    .fillMaxWidth(0.74f)
                    .neonPanel(24.dp, glow = true, borderColor = AppColors.Gold)
                    .padding(horizontal = 38.dp, vertical = 28.dp),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GoldText("ZARF ${selectedEnvelope + 1}", 24.sp)
                    GoldText(title, 42.sp)
                    Text(
                        body,
                        color = Color.White,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        lineHeight = 30.sp,
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                TvButton(stringResource(R.string.menu_new_game), onNewGame, Modifier.width(300.dp), primary = true, focusRequester = firstAction)
                TvButton(stringResource(R.string.main_menu), onMenu, Modifier.width(300.dp))
            }
        }
    }
}
