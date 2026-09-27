package com.ailecarki.tv.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ailecarki.tv.R
import com.ailecarki.tv.ui.components.LogoImage
import com.ailecarki.tv.ui.components.RequestFocus
import com.ailecarki.tv.ui.components.TvButton
import com.ailecarki.tv.ui.theme.AppColors

/**
 * Uygulama her açıldığında ana menüden önce görünen aile anma ekranı.
 * Bilerek seslendirme / otomatik geçiş yoktur: aile hazır olduğunda ÂMİN'e basar.
 */
@Composable
fun MemorialScreen(onAmen: () -> Unit) {
    val amenFocus = remember { FocusRequester() }
    RequestFocus(amenFocus, Unit)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        // Arka planı biraz sakinleştir: mevcut İstanbul sahnesi görünür kalsın ama metin öne çıksın.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x52030A25),
                            Color(0x74040A22),
                            Color(0x8A020716),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = maxHeight * 0.035f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LogoImage(height = 112.dp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.memorial_greeting),
                color = Color(0xFFFFE5A1),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .width((maxWidth * 0.64f).coerceAtMost(980.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xE30A143C), Color(0xEE050B23)),
                    ),
                    RoundedCornerShape(28.dp),
                )
                .border(2.dp, Color(0xFFFFD36A), RoundedCornerShape(28.dp))
                .padding(horizontal = 46.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.memorial_body_before_name))
                    pushStyle(SpanStyle(color = Color(0xFFFFD36A), fontWeight = FontWeight.Bold))
                    append(stringResource(R.string.memorial_name))
                    pop()
                    append(stringResource(R.string.memorial_body_after_name))
                },
                color = Color(0xFFF5F1E8),
                fontFamily = FontFamily.Serif,
                fontSize = 25.sp,
                lineHeight = 35.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.memorial_fatiha),
                color = Color(0xFFFFD36A),
                fontFamily = FontFamily.Serif,
                fontSize = 30.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = stringResource(R.string.memorial_accept),
                color = Color(0xFFE7DFD0),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
            )
        }

        TvButton(
            text = stringResource(R.string.memorial_amen),
            onClick = onAmen,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = maxHeight * 0.055f)
                .width(330.dp),
            primary = true,
            focusRequester = amenFocus,
            height = 72.dp,
            fontSize = 34.sp,
            corner = 22.dp,
        )
    }
}
