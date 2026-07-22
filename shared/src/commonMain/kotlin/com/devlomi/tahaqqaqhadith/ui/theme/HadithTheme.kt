package com.devlomi.tahaqqaqhadith.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import myapplication.shared.generated.resources.Res
import myapplication.shared.generated.resources.alex
import myapplication.shared.generated.resources.amiri
import myapplication.shared.generated.resources.literata
import myapplication.shared.generated.resources.noto_hinted
import myapplication.shared.generated.resources.noto_sans_arabic
import myapplication.shared.generated.resources.noto_sans_arabic_fixed
import myapplication.shared.generated.resources.noto_sans_reg
import myapplication.shared.generated.resources.noto_serif
import myapplication.shared.generated.resources.source_sans
import org.jetbrains.compose.resources.Font

// Tahaqqaq (Light)
val TahaqqaqPrimaryLight = Color(0xFF081C15)
val TahaqqaqSurfaceLight = Color(0xFFF9FAF2)
val TahaqqaqSurfaceContainerLight = Color(0xFFEDECE4)

// Nocturnal Scholar (Dark)
val TahaqqaqPrimaryDark = Color(0xFF2EB67D)
val TahaqqaqSurfaceDark = Color(0xFF0F1412)
val TahaqqaqSurfaceContainerDark = Color(0xFF181D1A)
val TahaqqaqOnSurfaceDark = Color(0xFFE1E3DE)

val TahaqqaqTeritary = Color(0xFF92F7C3)//TODO HANDLE DARK
val TahaqqaqOnTeritary = Color(0xFF00734D) //TODO HANDLE DARK
// Semantic colors
val AuthenticSuccess = Color(0xFF2EB67D)
val WeakError = Color(0xFFBA1A1A)
val ErrorBg = Color(0xFFFF9090)

// Compatibility aliases used by existing UI files.
val GoldenWheat = TahaqqaqPrimaryDark
val Forest = TahaqqaqSurfaceContainerDark
val ForestDark = TahaqqaqSurfaceDark
val Charcoal = TahaqqaqSurfaceDark
val CharcoalSoft = TahaqqaqSurfaceContainerDark
val DeepUmber = WeakError
val WheatLight = TahaqqaqOnSurfaceDark

private val hadithLightColorScheme = lightColorScheme(
    primary = TahaqqaqPrimaryLight,
    onPrimary = TahaqqaqSurfaceLight,
    secondary = AuthenticSuccess,
    onSecondary = Color.White,
    tertiary = TahaqqaqTeritary,
    onTertiary = TahaqqaqOnTeritary,
    background = TahaqqaqSurfaceLight,
    onBackground = TahaqqaqPrimaryLight,
    surface = TahaqqaqSurfaceLight,
    onSurface = TahaqqaqPrimaryLight,
    surfaceVariant = TahaqqaqSurfaceContainerLight,
    onSurfaceVariant = TahaqqaqPrimaryLight,
    error = WeakError,
    onError = Color.White,
    outline = TahaqqaqPrimaryLight.copy(alpha = 0.45f),
)

private val hadithDarkColorScheme = darkColorScheme(
    primary = TahaqqaqPrimaryDark,
    onPrimary = TahaqqaqSurfaceDark,
    secondary = TahaqqaqPrimaryDark,
    onSecondary = TahaqqaqSurfaceDark,
    tertiary = TahaqqaqSurfaceContainerDark,
    onTertiary = TahaqqaqOnSurfaceDark,
    background = TahaqqaqSurfaceDark,
    onBackground = TahaqqaqOnSurfaceDark,
    surface = TahaqqaqSurfaceDark,
    onSurface = TahaqqaqOnSurfaceDark,
    surfaceVariant = TahaqqaqSurfaceContainerDark,
    onSurfaceVariant = TahaqqaqOnSurfaceDark,
    error = WeakError,
    onError = Color.White,
    outline = TahaqqaqPrimaryDark.copy(alpha = 0.55f),
)

@Composable
fun HadithTheme(content: @Composable () -> Unit) {
    val amiriFontFamily = FontFamily(Font(resource = Res.font.amiri, weight = FontWeight.Normal))
    val notoSansArabic = FontFamily(Font(resource = Res.font.noto_hinted, weight = FontWeight.Normal))

    val colorScheme = if (isSystemInDarkTheme()) hadithDarkColorScheme else hadithLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography().withRoleFonts(
            headlineFamily = amiriFontFamily,
            bodyFamily = notoSansArabic,
            labelFamily = notoSansArabic,
        ),
    ) {
        content()
    }
}

private fun Typography.withRoleFonts(
    headlineFamily: FontFamily,
    bodyFamily: FontFamily,
    labelFamily: FontFamily,
): Typography {
    val scale = 1.2f
    return copy(
        displayLarge = displayLarge.copy(fontFamily = headlineFamily).upscale(scale),
        displayMedium = displayMedium.copy(fontFamily = headlineFamily).upscale(scale),
        displaySmall = displaySmall.copy(fontFamily = headlineFamily).upscale(scale),
        headlineLarge = headlineLarge.copy(fontFamily = headlineFamily).upscale(scale),
        headlineMedium = headlineMedium.copy(fontFamily = headlineFamily).upscale(scale),
        headlineSmall = headlineSmall.copy(fontFamily = headlineFamily).upscale(scale),
        titleLarge = titleLarge.copy(fontFamily = headlineFamily).upscale(scale),
        titleMedium = titleMedium.copy(fontFamily = headlineFamily).upscale(scale),
        titleSmall = titleSmall.copy(fontFamily = headlineFamily).upscale(scale),
        bodyLarge = bodyLarge.copy(fontFamily = bodyFamily).upscale(scale),
        bodyMedium = bodyMedium.copy(fontFamily = bodyFamily).upscale(scale),
        bodySmall = bodySmall.copy(fontFamily = bodyFamily).upscale(scale),
        labelLarge = labelLarge.copy(fontFamily = labelFamily).upscale(scale),
        labelMedium = labelMedium.copy(fontFamily = labelFamily).upscale(scale),
        labelSmall = labelSmall.copy(fontFamily = labelFamily).upscale(scale),
    )
}

private fun TextStyle.upscale(scale: Float): TextStyle {
    return copy(
        fontSize = fontSize * scale,
//        lineHeight = lineHeight * scale,
    )
}
