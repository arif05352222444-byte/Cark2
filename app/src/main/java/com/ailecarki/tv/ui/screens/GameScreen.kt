package com.ailecarki.tv.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.engine.PuzzleEngine
import com.ailecarki.tv.domain.engine.WheelEngine
import com.ailecarki.tv.domain.model.GamePhase
import com.ailecarki.tv.domain.model.GameState
import com.ailecarki.tv.domain.model.SegmentType
import com.ailecarki.tv.domain.rules.WheelConfig
import com.ailecarki.tv.ui.components.BannerCard
import com.ailecarki.tv.ui.components.CategoryPill
import com.ailecarki.tv.ui.components.EventBanner
import com.ailecarki.tv.ui.components.GameDialog
import com.ailecarki.tv.ui.components.LetterKeyboard
import com.ailecarki.tv.ui.components.PlayerScoreRow
import com.ailecarki.tv.ui.components.PuzzleBoard
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TextInputDialog
import com.ailecarki.tv.ui.components.TitleMarquee
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.WheelView
import com.ailecarki.tv.ui.components.goldTextStyle
import com.ailecarki.tv.ui.components.revealDurationMs
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.Dimens
import com.ailecarki.tv.ui.theme.formatScore
import com.ailecarki.tv.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import com.ailecarki.tv.ui.components.GameIconKind
import com.ailecarki.tv.ui.components.GoldText
import com.ailecarki.tv.ui.components.SparkleBurst
import com.ailecarki.tv.ui.components.WheelPodium
import com.ailecarki.tv.ui.components.neonPanel
import com.ailecarki.tv.ui.theme.GameFont

private val SpinEasing = CubicBezierEasing(0.12f, 0.8f, 0.2f, 1f)
private const val WHEEL_ENTER_MS = 600L
private const val WHEEL_MOVE_MS = 550
private const val WHEEL_RESULT_MS = 1600L
private const val ROUND_DIALOG_DELAY_MS = 1800L

private val ACTION_PHASES = setOf(GamePhase.PLAYER_TURN, GamePhase.PLAYER_ACTION)
private val SELECTION_PHASES = setOf(GamePhase.LETTER_SELECTION, GamePhase.VOWEL_SELECTION)
private val WHEEL_PHASES = setOf(GamePhase.WHEEL_SPINNING, GamePhase.WHEEL_RESULT)
private val BUSY_PHASES = setOf(GamePhase.WHEEL_SPINNING, GamePhase.WHEEL_RESULT, GamePhase.LETTER_REVEAL, GamePhase.FINAL_REVEAL)

@Composable
fun GameScreen(vm: GameViewModel, onExitToMenu: () -> Unit, onNewGame: () -> Unit) {
    val state by vm.state.collectAsStateWithLifecycle()
    // Ekrandan çıkılınca final sayacı temizlenir (kayıttan devamda yeniden başlar).
    DisposableEffect(Unit) { onDispose { vm.leaveGame() } }
    val s = state ?: return
    var showExit by remember { mutableStateOf(false) }
    // Çıkış diyaloğu kapanınca odak yeniden verilir (diyalog açıkken arka pencereye odak istenmez).
    var exitClosedCount by remember { mutableIntStateOf(0) }
    val closeExit: () -> Unit = { showExit = false; exitClosedCount += 1 }

    BackHandler {
        when (s.phase) {
            GamePhase.VOWEL_SELECTION -> vm.cancelVowel()
            in BUSY_PHASES -> Unit // animasyon sırasında geri tuşu yok sayılır
            GamePhase.GAME_COMPLETE -> onExitToMenu()
            else -> showExit = true
        }
    }

    when {
        s.phase == GamePhase.GAME_COMPLETE -> GameCompleteScreen(s, vm, onNewGame = onNewGame, onMenu = onExitToMenu)
        s.isFinal -> FinalScreen(s, vm, refocusKey = exitClosedCount)
        else -> MainGameContent(s, vm, refocusKey = exitClosedCount)
    }

    if (showExit) {
        val stay = remember { FocusRequester() }
        GameDialog(onDismiss = closeExit) {
            Text(stringResource(R.string.exit_title), style = goldTextStyle(32.sp))
            Text(stringResource(R.string.exit_body), color = AppColors.White, fontSize = 20.sp, textAlign = TextAlign.Center)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                TvButton(stringResource(R.string.exit_stay), closeExit, Modifier.width(240.dp), focusRequester = stay)
                TvButton(stringResource(R.string.exit_leave), { showExit = false; vm.leaveGame(); onExitToMenu() }, Modifier.width(240.dp))
            }
            RequestFocus(stay, Unit)
        }
    }
}

@Composable
private fun MainGameContent(s: GameState, vm: GameViewModel, refocusKey: Any?) {
    // Çarkın açısı turlar arasında korunur (çark kaldığı yerden döner).
    val rotation = remember { Animatable(-7.5f) }
    var keyboardFocusNonce by remember { mutableIntStateOf(0) }

    // Çevirme animasyonu: sonuç engine tarafından önceden belirlendi, animasyon onu gösterir.
    LaunchedEffect(s.spinCount) {
        if (s.phase == GamePhase.WHEEL_SPINNING) {
            val plan = vm.spinPlan() ?: return@LaunchedEffect
            val current = WheelEngine.positiveMod(rotation.value, 360f)
            rotation.snapTo(current)
            val target = vm.wheel.targetRotation(current, plan.targetIndex, plan.extraTurns, plan.jitter)
            delay(WHEEL_ENTER_MS)
            rotation.animateTo(target, tween(plan.durationMs, easing = SpinEasing))
            vm.onSpinFinished()
        }
    }
    // Sonuç gösterimi → etkisini uygula (harf seçimi / iflas / sıra geç / 2x).
    LaunchedEffect(s.phase, s.spinCount) {
        if (s.phase == GamePhase.WHEEL_RESULT) {
            val idx = s.spinSegmentIndex
            if (idx != null && vm.wheel.segmentIndexAt(rotation.value) != idx) {
                rotation.snapTo(vm.wheel.targetRotation(0f, idx, 0)) // kayıttan dönüldüyse
            }
            delay(WHEEL_RESULT_MS)
            vm.resolveWheelResult()
        }
    }
    // Harfler tek tek açılınca devam et.
    LaunchedEffect(s.phase, s.eventCounter) {
        if (s.phase == GamePhase.LETTER_REVEAL) {
            val letter = s.lastRevealedLetter
            val count = if (letter != null) PuzzleEngine.letterCount(s.puzzle.answer, letter) else 1
            delay(revealDurationMs(count, singleLetter = true))
            vm.finishReveal()
        }
    }

    val wheelActive = s.phase in WHEEL_PHASES
    val keyboardUp = s.phase in SELECTION_PHASES
    val wheelP by animateFloatAsState(
        if (wheelActive) 1f else 0f,
        tween(WHEEL_MOVE_MS, easing = FastOutSlowInEasing),
        label = "wheelMove",
    )
    val wheelOnTop by remember { derivedStateOf { wheelP > 0.01f } }
    val highlight = if (s.phase == GamePhase.WHEEL_RESULT) s.spinSegmentIndex else null

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val screenW = maxWidth
        val screenH = maxHeight
        val padH = screenW * 0.035f
        val padV = screenH * 0.035f
        val cardsH = 60.dp
        val cardsTop = screenH - padV - cardsH
        val keySize = 42.dp
        val kbH = keySize * 2 + 33.dp
        val kbTop = cardsTop - 10.dp - kbH
        val leftW = screenW * 0.27f
        val rightW = screenW * 0.22f
        val colTop = screenH * 0.2f
        val centerX = padH + leftW
        val centerW = screenW - padH * 2 - leftW - rightW - 16.dp

        // Çark geometrisi: dinlenirken solda, çevirirken ekranın ortasında büyük.
        val wheelBase = 420.dp
        val restSize = min(leftW - 10.dp, kbTop - colTop + 30.dp)
        val restScale = restSize / wheelBase
        val restCx = padH + leftW / 2f
        val restCy = colTop + restSize / 2f + 4.dp
        val midScale = (screenH * 0.84f) / wheelBase
        val midCx = screenW / 2f
        val midCy = screenH / 2f

        // Kaide çarkın dinlenme yerinde sabit kalır.
        WheelPodium(
            restSize * 1.05f,
            Modifier.offset(x = restCx - restSize * 0.525f, y = restCy + restSize * 0.4f),
        )

        TitleMarquee(
            stringResource(R.string.title),
            Modifier.align(Alignment.TopCenter).padding(top = padV).graphicsLayer { alpha = 1f - 0.55f * wheelP },
            fontSize = 34.sp,
        )

        // Orta: kategori, bulmaca, puan rozeti
        val bottom by animateDpAsState(if (keyboardUp) kbTop - 6.dp else cardsTop - 10.dp, tween(320), label = "colBottom")
        Column(
            Modifier
                .offset(x = centerX, y = colTop)
                .width(centerW)
                .height(bottom - colTop)
                .graphicsLayer { alpha = 1f - 0.5f * wheelP },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CategoryPill(s.puzzle.category)
            Spacer(Modifier.height(8.dp))
            key(s.puzzle.id) {
                PuzzleBoard(
                    s.puzzle.answer,
                    s.revealedLetters,
                    Modifier.fillMaxWidth().weight(1f),
                    maxLineChars = 12,
                    maxTile = 52.dp,
                )
            }
            AnimatedVisibility(
                visible = keyboardUp,
                enter = fadeIn(tween(250)) + expandVertically(tween(250)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(200)),
            ) {
                PointsBadge(s, vm)
            }
        }

        // Sağ: dikey aksiyon menüsü + kumanda yardımı
        Column(
            Modifier.offset(x = screenW - padH - rightW, y = colTop - 6.dp).width(rightW),
            verticalArrangement = Arrangement.spacedBy(9.dp),
        ) {
            ActionColumn(s, vm, refocusKey, onLetterClick = { keyboardFocusNonce += 1 })
            if (!keyboardUp) RemoteHelpPanel(Modifier.fillMaxWidth(), compact = true)
        }

        // Alt: SIRA SENDE + oyuncu kartları
        PlayerScoreRow(
            s.players,
            s.currentPlayerIndex,
            Modifier.offset(x = padH, y = cardsTop).width(screenW - padH * 2).height(cardsH),
            doubleActive = s.doubleActive,
            showTurnBadge = true,
            height = cardsH,
        )

        // Harf kartelası alttan kayarak gelir.
        AnimatedVisibility(
            visible = keyboardUp,
            modifier = Modifier.offset(y = kbTop).fillMaxWidth().zIndex(1f),
            enter = slideInVertically(tween(320)) { it } + fadeIn(tween(200)),
            exit = slideOutVertically(tween(260)) { it } + fadeOut(tween(200)),
        ) {
            KeyboardPanel(s, vm, focusKey = listOf(s.phase, refocusKey, keyboardFocusNonce), keySize = keySize, padH = padH)
        }

        // Çevirme sırasında sahne kararır…
        Box(
            Modifier
                .fillMaxSize()
                .zIndex(3f)
                .graphicsLayer { alpha = wheelP * 0.72f }
                .background(Color(0xFF030618)),
        )
        // …ve çark soldan büyüyerek merkeze gelir.
        Box(
            Modifier
                .zIndex(if (wheelOnTop) 4f else 0.5f)
                .size(wheelBase)
                .graphicsLayer {
                    val cx = lerp(restCx, midCx, wheelP)
                    val cy = lerp(restCy, midCy, wheelP)
                    translationX = (cx - wheelBase / 2f).toPx()
                    translationY = (cy - wheelBase / 2f).toPx()
                    val sc = restScale + (midScale - restScale) * wheelP
                    scaleX = sc
                    scaleY = sc
                },
        ) {
            WheelView(vm.wheel.segments, rotation = { rotation.value }, size = wheelBase, highlightIndex = highlight)
        }

        if (s.phase == GamePhase.WHEEL_RESULT) {
            WheelResultBanner(s, vm, Modifier.align(Alignment.BottomCenter).padding(bottom = padV + 4.dp).zIndex(5f))
        }

        EventBanner(s, Modifier.zIndex(6f))
    }

    if (s.phase == GamePhase.SOLVING) {
        TextInputDialog(
            title = stringResource(R.string.solve_title),
            subtitle = s.currentPlayer.name,
            initial = "",
            maxLength = 40,
            allowSpace = true,
            onConfirm = vm::submitSolve,
            onDismiss = vm::cancelSolve,
        )
    }

    if (s.phase == GamePhase.ROUND_COMPLETE) RoundCompleteDialog(s, vm)
}

@Composable
private fun ActionColumn(s: GameState, vm: GameViewModel, refocusKey: Any?, onLetterClick: () -> Unit) {
    val spin = remember { FocusRequester() }
    val solve = remember { FocusRequester() }
    val canSpin = vm.canSpin()
    if (s.phase in ACTION_PHASES) {
        RequestFocus(if (canSpin) spin else solve, s.phase, s.currentPlayerIndex, s.eventCounter, refocusKey)
    }
    val w = Modifier.fillMaxWidth()
    TvButton(stringResource(R.string.action_spin), vm::spin, w, enabled = canSpin, focusRequester = spin, height = 48.dp, fontSize = 19.sp, icon = GameIconKind.WHEEL, alignStart = true)
    TvButton(stringResource(R.string.action_letter), onLetterClick, w, enabled = s.phase in SELECTION_PHASES, height = 48.dp, fontSize = 19.sp, icon = GameIconKind.KEYBOARD, alignStart = true)
    TvButton(stringResource(R.string.action_vowel_short), vm::buyVowel, w, enabled = vm.canBuyVowel(), height = 48.dp, fontSize = 19.sp, icon = GameIconKind.BULB, alignStart = true)
    TvButton(stringResource(R.string.action_solve), vm::startSolve, w, enabled = vm.canSolve(), focusRequester = solve, height = 48.dp, fontSize = 19.sp, icon = GameIconKind.CHECK, alignStart = true)
}

@Composable
private fun KeyboardPanel(s: GameState, vm: GameViewModel, focusKey: Any?, keySize: Dp, padH: Dp) {
    Box(Modifier.fillMaxWidth().padding(horizontal = padH), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxWidth()
                .neonPanel(18.dp, glow = true)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            LetterKeyboard(
                isEnabled = { c -> vm.canSelectLetter(c) },
                onLetter = vm::chooseLetter,
                keySize = keySize,
                focusKey = focusKey,
            )
        }
    }
}

/** "500 PUAN" altın rozeti + altında "Harf seç". */
@Composable
private fun PointsBadge(s: GameState, vm: GameViewModel) {
    val seg = s.spinSegmentIndex?.let { vm.wheel.segments.getOrNull(it) }
    val title: String
    val sub: String
    when {
        s.phase == GamePhase.VOWEL_SELECTION -> {
            title = stringResource(R.string.vowel_badge)
            sub = stringResource(R.string.vowel_badge_sub, vm.rules.vowelCost)
        }
        seg?.type == SegmentType.JOKER -> {
            title = WheelConfig.LABEL_JOKER
            sub = stringResource(R.string.pick_free_letter, formatScore(vm.rules.jokerPoints))
        }
        seg != null -> {
            title = stringResource(R.string.points_value, formatScore(seg.value)) + if (s.doubleActive) " · 2X" else ""
            sub = stringResource(R.string.pick_letter)
        }
        else -> {
            title = ""
            sub = ""
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 6.dp)) {
        GoldCapsule { GoldText(title, 28.sp) }
        Box(
            Modifier
                .offset(y = (-4).dp)
                .neonPanel(12.dp)
                .padding(horizontal = 28.dp, vertical = 2.dp),
        ) {
            Text(sub, color = Color.White, fontSize = 16.sp, fontFamily = GameFont)
        }
    }
}

/** Referanstaki altın çerçeveli koyu kapsül (hale + parıltı noktaları). */
@Composable
fun GoldCapsule(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier
            .drawBehind {
                val r = CornerRadius(size.height / 2f)
                for (i in 3 downTo 1) {
                    val g = i * 4f * density
                    drawRoundRect(AppColors.Gold.copy(alpha = 0.1f * (4 - i)), Offset(-g, -g), Size(size.width + g * 2, size.height + g * 2), CornerRadius(r.x + g))
                }
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xFF3A2200), Color(0xFF120A00))), cornerRadius = r)
                drawRoundRect(Brush.verticalGradient(listOf(Color(0xFFFFE58A), Color(0xFFE08A00))), cornerRadius = r, style = Stroke(4.dp.toPx()))
                drawCircle(Color(0xFFFFF6C2), 2.5.dp.toPx(), Offset(size.height * 0.45f, size.height / 2f))
                drawCircle(Color(0xFFFFF6C2), 2.5.dp.toPx(), Offset(size.width - size.height * 0.45f, size.height / 2f))
            }
            .padding(horizontal = 30.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) { content() }
}

@Composable
private fun WheelResultBanner(s: GameState, vm: GameViewModel, modifier: Modifier) {
    val segment = s.spinSegmentIndex?.let { vm.wheel.segments.getOrNull(it) } ?: return
    val (title, color) = when (segment.type) {
        SegmentType.POINTS -> stringResource(R.string.points_value, formatScore(segment.value)) to AppColors.Gold
        SegmentType.BANKRUPT -> stringResource(R.string.ev_bankrupt) to AppColors.Red
        SegmentType.LOSE_TURN -> WheelConfig.LABEL_LOSE_TURN to AppColors.Cyan
        SegmentType.DOUBLE -> stringResource(R.string.ev_double) to AppColors.Fuchsia
        SegmentType.JOKER -> (WheelConfig.LABEL_JOKER + "!") to AppColors.Gold
    }
    val sub = when (segment.type) {
        SegmentType.POINTS, SegmentType.JOKER -> stringResource(R.string.pick_letter)
        else -> null
    }
    Box(modifier, contentAlignment = Alignment.Center) {
        if (segment.type == SegmentType.JOKER || segment.type == SegmentType.DOUBLE) {
            val burst = remember { Animatable(0f) }
            LaunchedEffect(s.spinCount) { burst.snapTo(0f); burst.animateTo(1f, tween(1000)) }
            SparkleBurst(color, { burst.value }, Modifier.size(420.dp))
        }
        BannerCard(title, sub, color)
    }
}

@Composable
private fun RoundCompleteDialog(s: GameState, vm: GameViewModel) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(s.round) {
        delay(ROUND_DIALOG_DELAY_MS)
        visible = true
    }
    if (!visible) return
    GameDialog(onDismiss = {}, dismissOnBack = false, maxWidth = 820.dp) {
        Text(stringResource(R.string.round_complete), style = goldTextStyle(40.sp))
        Text(s.puzzle.answer, color = AppColors.White, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
        s.roundWinnerIndex?.let {
            Text(stringResource(R.string.round_winner, s.players[it].name), color = AppColors.GoldLight, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        PlayerScoreRow(s.players, s.roundWinnerIndex, Modifier.width(760.dp))
        var ready by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) { delay(1200); ready = true }
        Box(Modifier.height(64.dp), contentAlignment = Alignment.Center) {
            if (ready) {
                val next = remember { FocusRequester() }
                TvButton(
                    stringResource(if (vm.isLastNormalRound()) R.string.go_final else R.string.next_round),
                    vm::nextRound,
                    Modifier.width(320.dp),
                    primary = true,
                    focusRequester = next,
                )
                RequestFocus(next, Unit)
            }
        }
    }
}
