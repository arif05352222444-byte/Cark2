package com.ailecarki.tv.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.domain.rules.WheelConfig
import com.ailecarki.tv.ui.components.GameIcon
import com.ailecarki.tv.ui.components.GameIconKind
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TitleMarquee
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.components.WheelPodium
import com.ailecarki.tv.ui.components.WheelView
import com.ailecarki.tv.ui.theme.AppColors

@Composable
fun HomeScreen(
    hasSavedGame: Boolean,
    onNewGame: () -> Unit,
    onContinue: () -> Unit,
    onSettings: () -> Unit,
    onExit: () -> Unit,
) {
    val first = remember { FocusRequester() }
    RequestFocus(first, Unit)
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val padH = maxWidth * 0.035f
        val padV = maxHeight * 0.035f
        val wheel = (maxHeight * 0.62f)
        // Sol: büyük dekoratif çark + kaide
        Box(Modifier.offset(x = padH, y = maxHeight * 0.2f)) {
            WheelPodium(wheel * 1.05f, Modifier.offset(x = -(wheel * 0.025f), y = wheel * 0.9f))
            WheelView(WheelConfig.DEFAULT, rotation = { -7.5f }, size = wheel)
        }
        // Üst orta: logo + slogan
        Column(
            Modifier.align(Alignment.TopCenter).padding(top = padV),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TitleMarquee(stringResource(R.string.title), fontSize = 50.sp)
            Spacer(Modifier.height(10.dp))
            Text(
                stringResource(R.string.slogan),
                color = Color(0xFFE6EEFF),
                fontSize = 21.sp,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.SemiBold,
            )
        }
        // Sağ: menü
        Column(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = padH, top = 70.dp)
                .width(300.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val w = Modifier.width(300.dp)
            TvButton(stringResource(R.string.menu_new_game), onNewGame, w, focusRequester = first, height = 62.dp, fontSize = 26.sp, icon = GameIconKind.PLAY, alignStart = true)
            TvButton(stringResource(R.string.menu_continue), onContinue, w, enabled = hasSavedGame, height = 62.dp, fontSize = 26.sp, icon = GameIconKind.REFRESH, alignStart = true)
            TvButton(stringResource(R.string.menu_settings), onSettings, w, height = 62.dp, fontSize = 26.sp, icon = GameIconKind.GEAR, alignStart = true)
            TvButton(stringResource(R.string.menu_exit), onExit, w, height = 62.dp, fontSize = 26.sp, icon = GameIconKind.DOOR, alignStart = true)
        }
        // Alt şerit
        Row(
            Modifier.align(Alignment.BottomCenter).padding(bottom = padV),
            horizontalArrangement = Arrangement.spacedBy(36.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FooterItem(GameIconKind.PERSON, stringResource(R.string.footer_1))
            FooterItem(GameIconKind.STAR, stringResource(R.string.footer_2))
            FooterItem(GameIconKind.CHECK, stringResource(R.string.footer_3))
        }
    }
}

@Composable
private fun FooterItem(icon: GameIconKind, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        GameIcon(icon, AppColors.TextMuted, size = 20.dp)
        Spacer(Modifier.width(8.dp))
        Text(text, color = AppColors.TextMuted, fontSize = 15.sp, fontWeight = FontWeight.Medium)
    }
}
