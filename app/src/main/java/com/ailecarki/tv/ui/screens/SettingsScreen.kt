package com.ailecarki.tv.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ailecarki.tv.R
import com.ailecarki.tv.audio.SoundId
import com.ailecarki.tv.data.AppSettings
import com.ailecarki.tv.domain.rules.WheelConfig
import com.ailecarki.tv.ui.components.GameIcon
import com.ailecarki.tv.ui.components.GameIconKind
import com.ailecarki.tv.ui.components.LocalSoundPlayer
import com.ailecarki.tv.ui.components.PanelState
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TitleMarquee
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.WheelView
import com.ailecarki.tv.ui.components.drawGamePanel
import com.ailecarki.tv.ui.components.goldTextStyle
import com.ailecarki.tv.ui.components.neonPanel
import com.ailecarki.tv.ui.theme.AppColors
import com.ailecarki.tv.ui.theme.GameFont
import com.ailecarki.tv.ui.viewmodel.SettingsViewModel

private fun <T> List<T>.cycle(current: T, step: Int): T {
    val i = indexOf(current).coerceAtLeast(0)
    return this[((i + step) % size + size) % size]
}

@Composable
fun SettingsScreen(vm: SettingsViewModel, onBack: () -> Unit) {
    val s by vm.settings.collectAsStateWithLifecycle()
    val first = remember { FocusRequester() }
    RequestFocus(first, Unit)
    BackHandler { onBack() }
    val on = stringResource(R.string.on)
    val off = stringResource(R.string.off)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val padV = maxHeight * 0.035f
        // Solda yarısı görünen dekoratif çark
        WheelView(WheelConfig.DEFAULT, rotation = { -7.5f }, size = maxHeight * 0.52f, modifier = Modifier.offset(x = -(maxHeight * 0.26f), y = maxHeight * 0.3f))

        Column(
            Modifier.fillMaxSize().padding(top = padV, bottom = padV),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleMarquee(stringResource(R.string.title), fontSize = 32.sp)
            Spacer(Modifier.height(10.dp))
            Column(
                Modifier
                    .width(640.dp)
                    .neonPanel(18.dp, glow = true)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GameIcon(GameIconKind.GEAR, AppColors.Gold, size = 26.dp)
                    Spacer(Modifier.width(12.dp))
                    Text(stringResource(R.string.settings_title), style = goldTextStyle(32.sp))
                    Spacer(Modifier.width(12.dp))
                    GameIcon(GameIconKind.GEAR, AppColors.Gold, size = 26.dp)
                }
                SettingRow(GameIconKind.MIC, stringResource(R.string.set_host), { ValueText(if (s.hostVoice) on else off) },
                    onChange = { vm.update { it.copy(hostVoice = !it.hostVoice) } }, requester = first)
                SettingRow(GameIconKind.MUSIC, stringResource(R.string.set_music), { LevelBars(s.musicLevel) },
                    onChange = { d -> vm.update { it.copy(musicLevel = (it.musicLevel + d).coerceIn(0, AppSettings.MAX_LEVEL)) } })
                SettingRow(GameIconKind.SPEAKER, stringResource(R.string.set_effects), { LevelBars(s.effectsLevel) },
                    onChange = { d -> vm.update { it.copy(effectsLevel = (it.effectsLevel + d).coerceIn(0, AppSettings.MAX_LEVEL)) } })
                SettingRow(GameIconKind.BELL, stringResource(R.string.set_ui), { ValueText(if (s.uiSounds) on else off) },
                    onChange = { vm.update { it.copy(uiSounds = !it.uiSounds) } })
                SettingRow(GameIconKind.LIST, stringResource(R.string.set_rounds), { ValueText(s.rounds.toString()) },
                    onChange = { d -> vm.update { it.copy(rounds = AppSettings.ROUND_OPTIONS.cycle(it.rounds, d)) } })
                SettingRow(GameIconKind.COINS, stringResource(R.string.set_vowel), { ValueText(s.vowelCost.toString()) },
                    onChange = { d -> vm.update { it.copy(vowelCost = AppSettings.VOWEL_COST_OPTIONS.cycle(it.vowelCost, d)) } })
                SettingRow(GameIconKind.TIMER, stringResource(R.string.set_final), { ValueText(stringResource(R.string.seconds, s.finalSeconds)) },
                    onChange = { d -> vm.update { it.copy(finalSeconds = AppSettings.FINAL_SECONDS_OPTIONS.cycle(it.finalSeconds, d)) } })
            }
            Spacer(Modifier.height(10.dp))
            TvButton(stringResource(R.string.back), onBack, Modifier.width(260.dp), height = 46.dp, fontSize = 22.sp, icon = GameIconKind.BACK)
            Spacer(Modifier.height(6.dp))
            Text(stringResource(R.string.settings_hint), color = AppColors.TextMuted, fontSize = 13.sp)
        }
    }
}

@Composable
private fun ValueText(text: String) {
    Text(text, color = Color.White, fontSize = 17.sp, fontFamily = GameFont)
}

/** Modern seviye göstergesi: yükselen altın çubuklar (10 adım). */
@Composable
private fun LevelBars(level: Int) {
    Canvas(Modifier.width(118.dp).height(22.dp)) {
        val n = AppSettings.MAX_LEVEL
        val gap = 3.dp.toPx()
        val bw = (size.width - gap * (n - 1)) / n
        for (i in 0 until n) {
            val hh = size.height * (0.4f + 0.6f * (i + 1) / n)
            val on = i < level
            drawRoundRect(
                if (on) Brush.verticalGradient(listOf(Color(0xFFFFE58A), Color(0xFFFFB321)), size.height - hh, size.height)
                else Brush.verticalGradient(listOf(Color(0xFF3A4A8A), Color(0xFF26315F))),
                Offset(i * (bw + gap), size.height - hh),
                Size(bw, hh),
                CornerRadius(2.dp.toPx()),
            )
        }
    }
}

/** ◀ ▶ değeri değiştirir (onChange(-1/+1)), OK bir sonraki değere geçer. */
@Composable
private fun SettingRow(
    icon: GameIconKind,
    label: String,
    value: @Composable () -> Unit,
    onChange: (Int) -> Unit,
    requester: FocusRequester? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val focused by interaction.collectIsFocusedAsState()
    val sound = LocalSoundPlayer.current
    val scale by animateFloatAsState(if (focused) 1.025f else 1f, label = "settingScale")
    LaunchedEffect(focused) { if (focused) sound(SoundId.UI_MOVE) }
    Row(
        Modifier
            .fillMaxWidth()
            .height(36.dp)
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .drawBehind {
                drawGamePanel(if (focused) PanelState.FOCUSED else PanelState.NORMAL, 12.dp.toPx())
                if (focused) {
                    drawRoundRect(
                        Brush.verticalGradient(listOf(Color(0xFF1A2F9A), Color(0xFF0A1560))),
                        Offset(3.dp.toPx(), 3.dp.toPx()), Size(size.width - 6.dp.toPx(), size.height - 6.dp.toPx()),
                        CornerRadius(9.dp.toPx()),
                    )
                }
            }
            .onKeyEvent { e ->
                if (e.type != KeyEventType.KeyDown) return@onKeyEvent false
                when (e.key) {
                    Key.DirectionLeft -> { sound(SoundId.UI_SELECT); onChange(-1); true }
                    Key.DirectionRight -> { sound(SoundId.UI_SELECT); onChange(1); true }
                    else -> false
                }
            }
            .then(if (requester != null) Modifier.focusRequester(requester) else Modifier)
            .clickable(interactionSource = interaction, indication = null) { sound(SoundId.UI_SELECT); onChange(1) }
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier.size(28.dp).border(1.5.dp, if (focused) AppColors.Gold else AppColors.Neon, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            GameIcon(icon, if (focused) AppColors.Gold else Color.White, size = 17.dp)
        }
        Spacer(Modifier.width(14.dp))
        Text(label, color = if (focused) AppColors.GoldLight else Color.White, fontSize = 17.sp, fontFamily = GameFont)
        Spacer(Modifier.weight(1f))
        ArrowBox(GameIconKind.BACK, focused)
        Spacer(Modifier.width(8.dp))
        Box(
            Modifier
                .width(140.dp)
                .height(28.dp)
                .background(Color(0xFF081050), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0x665AA8FF), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center,
        ) { value() }
        Spacer(Modifier.width(8.dp))
        ArrowBox(GameIconKind.PLAY, focused)
    }
}

@Composable
private fun ArrowBox(icon: GameIconKind, focused: Boolean) {
    Box(
        Modifier
            .size(width = 36.dp, height = 28.dp)
            .background(Brush.verticalGradient(listOf(Color(0xFF2A55D8), Color(0xFF10247A))), RoundedCornerShape(8.dp))
            .border(1.dp, if (focused) AppColors.Gold else AppColors.Neon, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        GameIcon(icon, Color.White, size = 14.dp)
    }
}
