package dev.keego.musicplayer.config.theme

/**
 * author: https://github.com/touchlab/DroidconKotlin/blob/70d5658416a6b69565b0bd2997f658a7a604b77b/shared-ui/src/commonMain/kotlin/co.touchlab.droidcon/ui/theme/Typography.kt#L41
 */

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.keego.musicplayer.R

internal object Typography {
    private val montserratFontFamily = FontFamily(
        Font(
            resId = R.font.montserrat,
            weight = FontWeight.Normal
        ),
        Font(
            resId = R.font.montserrat_medium,
            weight = FontWeight.Medium
        ),
        Font(
            resId = R.font.montserrat_semibold,
            weight = FontWeight.SemiBold
        ),
        Font(
            resId = R.font.montserrat_bold,
            weight = FontWeight.Bold
        )
    )

    val typography = Typography(
        headlineLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 32.sp
        ),
        headlineMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 28.sp
        ),
        headlineSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 22.sp,
            letterSpacing = 0.15.sp
        ),
        titleLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp
        ),
        titleMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        ),
        titleSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        bodyLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        ),
        bodyMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        ),
        bodySmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        ),
        labelLarge = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        ),
        labelMedium = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp
        ),
        labelSmall = TextStyle(
            fontFamily = montserratFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 10.sp,
            letterSpacing = 1.5.sp
        )
    )
}