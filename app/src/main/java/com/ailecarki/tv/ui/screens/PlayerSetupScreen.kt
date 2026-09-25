package com.ailecarki.tv.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ailecarki.tv.R
import com.ailecarki.tv.audio.SoundId
import com.ailecarki.tv.domain.rules.WheelConfig
import com.ailecarki.tv.ui.components.GameIcon
import com.ailecarki.tv.ui.components.GameIconKind
import com.ailecarki.tv.ui.components.LocalSoundPlayer
import com.ailecarki.tv.ui.components.PanelState
import com.ailecarki.tv.ui.components.TextInputDialog
import com.ailecarki.tv.ui.components.TitleMarquee
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.WheelPodium
import com.ailecarki.tv.ui.components.WheelView
import com.ailecarki.tv.ui.components.drawGamePanel
import com.ailecarki.tv.ui.components.goldTextStyle
import com.ailecarki.tv.ui.components.neonPanel
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import com.ailecarki.tv.ui.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerSetupScreen(vm: GameViewModel, onBack: () -> Unit, onStarted: () -> Unit) {
    val names by vm.names.collectAsStateWithLifecycle()
    var editing by remember { mutableStateOf<Int?>(null) }
    var triedStart by remember { mutableStateOf(false) }
    val rowRequesters = remember { List(6) { FocusRequester() } }
    var lastEdited by remember { mutableIntStateOf(0) }
    BackHandler { onBack() }

    // İlk açılışta ve isim diyaloğu kapanınca odak düzenlenen satıra döner.
    LaunchedEffect(editing == null) {
        if (editing == null && names.isNotEmpty()) {
            delay(80)
            runCatching { rowRequesters[lastEdited.coerceIn(0, names.size - 1)].requestFocus() }
        }
    }
    LaunchedEffect(names) { if (vm.namesValid()) triedStart = false }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val w = maxWidth
        val h = maxHeight
        val padH = w * 0.035f
        val padV = h * 0.035f

        // Sol dekoratif çark
        val wheel = h * 0.46f
        Box(Modifier.offset(x = padH - 6.dp, y = h * 0.24f)) {
            WheelPodium(wheel * 1.05f, Modifier.offset(x = -(wheel * 0.025f), y = wheel * 0.9f))
            WheelView(WheelConfig.DEFAULT, rotation = { -7.5f }, size = wheel)
        }

        TitleMarquee(stringResource(R.string.title), Modifier.align(Alignment.TopCenter).padding(top = padV), fontSize = 36.sp)

        // Orta panel: oyuncu isimleri
        Column(
            Modifier
                .offset(x = w * 0.29f, y = h * 0.205f)
                .width(w * 0.44f)
                .neonPanel(20.dp, glow = true)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            Text(stringResource(R.string.setup_title), style = goldTextStyle(30.sp))
            Text(stringResource(R.string.setup_subtitle), color = Color(0xFFDCE6FF), fontSize = 14.sp, fontStyle = FontStyle.Italic)
            names.forEachIndexed { i, name ->
                NameRow(i, name, rowRequesters[i]) { lastEdited = i; editing = i }
            }
        }

        // Sağ: kumanda yardımı
        RemoteHelpPanel(
            Modifier
                .offset(x = w * 0.755f, y = h * 0.235f)
                .width(w * 0.21f),
        )

        // Alt butonlar
        Row(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = h * 0.745f),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TvButton(
                stringResource(R.string.setup_add), vm::addPlayer, Modifier.width(230.dp),
                enabled = names.size < vm.rules.maxPlayers, height = 54.dp, fontSize = 20.sp, icon = GameIconKind.PLUS,
            )
            TvButton(stringResource(R.string.setup_random), vm::fillRandomNames, Modifier.width(230.dp), height = 54.dp, fontSize = 20.sp, icon = GameIconKind.SHUFFLE)
            TvButton(
                stringResource(R.string.setup_start),
                onClick = { triedStart = true; vm.startNewGame(onStarted) },
                modifier = Modifier.width(300.dp),
                primary = true,
                height = 58.dp,
                fontSize = 25.sp,
                icon = GameIconKind.PLAY,
            )
        }

        TvButton(
            stringResource(R.string.back), onBack,
            Modifier.align(Alignment.BottomStart).padding(start = padH, bottom = padV).width(150.dp),
            height = 44.dp, fontSize = 18.sp, icon = GameIconKind.BACK,
        )

        val warning = when {
            vm.hasDuplicateNames() -> stringResource(R.string.setup_duplicate_warning)
            triedStart && !vm.namesValid() -> stringResource(R.string.setup_empty_warning)
            else -> null
        }
        Text(
            warning ?: stringResource(R.string.footer_2),
            color = if (warning != null) AppColors.GoldLight else AppColors.TextMuted,
            fontSize = if (warning != null) 18.sp else 15.sp,
            fontWeight = if (warning != null) FontWeight.Bold else FontWeight.Medium,
            fontStyle = if (warning != null) FontStyle.Normal else FontStyle.Italic,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = padV + 8.dp),
        )
    }

    editing?.let { i ->
        TextInputDialog(
            title = stringResource(R.string.setup_name_dialog, i + 1),
            initial = names.getOrElse(i) { "" },
            maxLength = vm.rules.maxNameLength,
            allowSpace = true,
            onConfirm = { vm.setName(i, it); editing = null },
            onDismiss = { editing = null },
        )
    }
}

@Composable
private fun NameRow(index: Int, name: String, requester: FocusRequester, onClick: () -> Unit) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val sound = LocalSoundPlayer.current
    val scale by animateFloatAsState(if (focused) 1.03f else 1f, label = "rowScale")
    LaunchedEffect(focused) { if (focused) sound(SoundId.UI_MOVE) }
    val color = AppColors.PlayerColors[index % AppColors.PlayerColors.size]
    Row(
        Modifier
            .fillMaxWidth()
            .height(42.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .drawBehind {
                drawGamePanel(if (focused) PanelState.FOCUSED else PanelState.NORMAL, 14.dp.toPx())
                if (focused) {
                    // Odakta içi koyu kalsın, sadece altın çerçeve + hale (referanstaki gibi)
                    drawRoundRect(
                        Brush.verticalGradient(listOf(Color(0xFF1A2F9A), Color(0xFF0A1560))),
                        topLeft = androidx.compose.ui.geometry.Offset(3.dp.toPx(), 3.dp.toPx()),
                        size = androidx.compose.ui.geometry.Size(size.width - 6.dp.toPx(), size.height - 6.dp.toPx()),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(11.dp.toPx()),
                    )
                }
            }
            .focusRequester(requester)
            .clickable(interactionSource = interaction, indication = null) { sound(SoundId.UI_SELECT); onClick() }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(34.dp)
                .background(Brush.verticalGradient(listOf(lerp(color, Color.White, 0.3f), color, lerp(color, Color.Black, 0.35f))), CircleShape)
                .border(2.dp, Color(0xCCFFFFFF), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            GameIcon(GameIconKind.PERSON, Color.White, size = 20.dp)
        }
        Spacer(Modifier.width(12.dp))
        Text(
            stringResource(R.string.setup_player, index + 1),
            color = if (focused) AppColors.GoldLight else Color.White,
            fontSize = 17.sp,
            fontFamily = GameFont,
            modifier = Modifier.width(104.dp),
        )
        val fieldShape = RoundedCornerShape(9.dp)
        Box(
            Modifier
                .weight(1f)
                .height(31.dp)
                .background(
                    if (name.isBlank()) Brush.verticalGradient(listOf(Color(0xFF28325A), Color(0xFF1B2344)))
                    else Brush.verticalGradient(listOf(Color.White, Color(0xFFE6EBFA))),
                    fieldShape,
                )
                .border(1.5.dp, if (name.isBlank()) Color(0x886D7AB0) else Color(0x664F7DFF), fieldShape)
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (name.isBlank()) {
                Text(stringResource(R.string.setup_name_hint), color = Color(0xFF9AA6D0), fontSize = 17.sp, fontStyle = FontStyle.Italic)
            } else {
                Text(name, color = AppColors.Navy, fontSize = 19.sp, fontFamily = GameFont)
            }
        }
    }
}

/** Kumanda yardımı: yön tuşları + OK çizimi. */
@Composable
fun RemoteHelpPanel(modifier: Modifier = Modifier, compact: Boolean = false) {
    Column(
        modifier
            .neonPanel(16.dp)
            .padding(horizontal = 12.dp, vertical = if (compact) 8.dp else 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp),
    ) {
        if (!compact) Text(stringResource(R.string.remote_help_title), color = Color.White, fontSize = 16.sp, fontFamily = GameFont)
        DPad(if (compact) 64.dp else 104.dp)
        Text(stringResource(R.string.remote_help_move), color = Color.White, fontSize = if (compact) 12.sp else 15.sp, fontWeight = FontWeight.Medium)
        if (!compact) {
            Text(stringResource(R.string.remote_help_ok), color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x555AA8FF)))
            Row(verticalAlignment = Alignment.CenterVertically) {
                GameIcon(GameIconKind.BULB, AppColors.Gold, size = 24.dp)
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.remote_help_min), color = Color(0xFFDCE6FF), fontSize = 13.sp, fontStyle = FontStyle.Italic)
            }
        }
    }
}

@Composable
private fun DPad(size: androidx.compose.ui.unit.Dp) {
    Box(Modifier.size(size)) {
        val k = size / 3f
        DPadKey(k, 1, 0, -90f)
        DPadKey(k, 0, 1, 180f)
        DPadKey(k, 2, 1, 0f)
        DPadKey(k, 1, 2, 90f)
        Box(
            Modifier
                .offset(k, k)
                .size(k * 0.92f)
                .background(Color(0xFF081050), CircleShape)
                .border(2.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("OK", color = Color.White, fontSize = (k.value * 0.34f).sp, fontFamily = GameFont)
        }
    }
}

@Composable
private fun DPadKey(k: androidx.compose.ui.unit.Dp, x: Int, y: Int, rotate: Float) {
    val shape = RoundedCornerShape(8.dp)
    Box(
        Modifier
            .offset(k * x, k * y)
            .size(k * 0.92f)
            .background(Brush.verticalGradient(listOf(Color(0xFF2A55D8), Color(0xFF10247A))), shape)
            .border(1.2.dp, AppColors.Neon, shape),
        contentAlignment = Alignment.Center,
    ) {
        GameIcon(GameIconKind.PLAY, Color.White, Modifier.graphicsLayer { rotationZ = rotate }, size = k * 0.45f)
    }
}
