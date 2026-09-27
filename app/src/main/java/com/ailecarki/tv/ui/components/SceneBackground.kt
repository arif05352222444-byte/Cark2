package com.ailecarki.tv.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

/**
 * Stüdyo sahnesi arka plan görseli (AileCarki_ArtPack_v1: bg_menu / bg_game).
 * Ekranı 16:9 kırparak doldurur; ekran değişince yumuşak geçiş yapar. Statik → TV Box'ı yormaz.
 * (Eski kodla çizilen StageBackground dosyası yedek olarak duruyor, kullanılmıyor.)
 */
@Composable
fun SceneBackground(@DrawableRes res: Int, modifier: Modifier = Modifier) {
    Crossfade(targetState = res, animationSpec = tween(450), label = "sceneBg", modifier = modifier.fillMaxSize()) { r ->
        Image(
            painter = painterResource(r),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
    }
}
