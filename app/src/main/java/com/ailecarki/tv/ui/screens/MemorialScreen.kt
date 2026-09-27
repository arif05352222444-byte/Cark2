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

/**
 * Uygulamanın ilk ekranı. Bilerek sessizdir: seslendirme, yarışma müziği ve otomatik geçiş yoktur.
 * Dikey yerleşim üç gerçek bölgeye ayrılır; metin ve ÂMİN butonu hiçbir TV yoğunluğunda üst üste binmez.
 */
@Composable
fun MemorialScreen(onAmen: () -> Unit) {
    val amenFocus = remember { FocusRequester() }
    RequestFocus(amenFocus, Unit)

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < 650.dp
        val bodySize = if (compact) 17.sp else 22.sp
        val bodyLine = if (compact) 23.sp else 31.sp
        val fatihaSize = if (compact) 21.sp else 28.sp
        val cardWidth = (maxWidth * if (compact) 0.78f else 0.70f).coerceAtMost(1060.dp)

        // Yarışma sahnesini sakinleştirir; anma ekranı ana oyun kadar parlak görünmez.
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0x72040A24),
                            Color(0xB0060B20),
                            Color(0xD2020613),
                        ),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = maxWidth * 0.035f,
                    end = maxWidth * 0.035f,
                    top = if (compact) 8.dp else 14.dp,
                    bottom = if (compact) 10.dp else 16.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 1) ÜST: logo + selamlama. Sabit bölge; gövdeye asla taşmaz.
            Column(
                modifier = Modifier.height(if (compact) 88.dp else 116.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LogoImage(height = if (compact) 58.dp else 80.dp)
                Text(
                    text = stringResource(R.string.memorial_greeting),
                    color = Color(0xFFFFE7A6),
                    fontSize = if (compact) 17.sp else 23.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = if (compact) 0.9.sp else 1.4.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(if (compact) 4.dp else 8.dp))

            // 2) ORTA: metin alanı yalnızca kalan yüksekliği kullanır.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .width(cardWidth)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xEC152050), Color(0xF509102D), Color(0xF604091C)),
                        ),
                        RoundedCornerShape(if (compact) 22.dp else 30.dp),
                    )
                    .border(
                        if (compact) 1.5.dp else 2.dp,
                        Color(0xFFFFD36A),
                        RoundedCornerShape(if (compact) 22.dp else 30.dp),
                    )
                    .padding(
                        horizontal = if (compact) 30.dp else 50.dp,
                        vertical = if (compact) 12.dp else 20.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.memorial_body_before_name),
                    color = Color(0xFFF6F2E9),
                    fontFamily = FontFamily.Serif,
                    fontSize = bodySize,
                    lineHeight = bodyLine,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(if (compact) 7.dp else 12.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Başta kıymetli babamız ")
                        pushStyle(SpanStyle(color = Color(0xFFFFD36A), fontWeight = FontWeight.Bold))
                        append(stringResource(R.string.memorial_name))
                        pop()
                        append(" olmak üzere, ebediyete uğurladığımız tüm aile büyüklerimizi sevgi, özlem ve rahmetle anıyoruz.")
                    },
                    color = Color(0xFFF6F2E9),
                    fontFamily = FontFamily.Serif,
                    fontSize = bodySize,
                    lineHeight = bodyLine,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium,
                )
                Spacer(Modifier.height(if (compact) 8.dp else 14.dp))
                Text(
                    text = stringResource(R.string.memorial_fatiha),
                    color = Color(0xFFFFD36A),
                    fontFamily = FontFamily.Serif,
                    fontSize = fatihaSize,
                    lineHeight = if (compact) 25.sp else 34.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(if (compact) 2.dp else 5.dp))
                Text(
                    text = stringResource(R.string.memorial_accept),
                    color = Color(0xFFE7DFD0),
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = if (compact) 14.sp else 18.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(Modifier.height(if (compact) 10.dp else 14.dp))

            // 3) ALT: ÂMİN kendi sabit bölgesinde. Metnin üstüne binmesi geometrik olarak mümkün değil.
            TvButton(
                text = stringResource(R.string.memorial_amen),
                onClick = onAmen,
                modifier = Modifier.width(if (compact) 270.dp else 330.dp),
                primary = true,
                focusRequester = amenFocus,
                height = if (compact) 54.dp else 66.dp,
                fontSize = if (compact) 25.sp else 31.sp,
                corner = 22.dp,
            )
        }
    }
}
