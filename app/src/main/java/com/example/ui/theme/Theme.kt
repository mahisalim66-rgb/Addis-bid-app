package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// =========================================================================
// Centralized Material3 ColorScheme with Ethiopian-Inspired Palette
// Blue (Abbay/Telebirr), Green (Adey/Fertility), Yellow (Sun/Gold), Red (Strength)
// =========================================================================

val EthiopianDarkColorScheme = darkColorScheme(
    // 1. Blue (Primary Brand)
    primary = EthiopianBlueLight,
    onPrimary = Color(0xFF002244),
    primaryContainer = EthiopianBlueContainerDark,
    onPrimaryContainer = Color(0xFFBAE6FD),

    // 2. Yellow (Secondary / Gold Rewards)
    secondary = EthiopianYellowPrimary,
    onSecondary = Color(0xFF3B2700),
    secondaryContainer = EthiopianYellowContainerDark,
    onSecondaryContainer = Color(0xFFFEF08A),

    // 3. Green (Tertiary / Verification & Growth)
    tertiary = EthiopianGreenLight,
    onTertiary = Color(0xFF00381B),
    tertiaryContainer = EthiopianGreenContainerDark,
    onTertiaryContainer = Color(0xFFA7F3D0),

    // 4. Red (Error / Live Countdown / Urgent)
    error = EthiopianRedLight,
    onError = Color(0xFF450A0A),
    errorContainer = EthiopianRedContainerDark,
    onErrorContainer = Color(0xFFFECACA),

    // Surfaces & Obsidian Black Canvas
    background = ObsidianMidnight,
    onBackground = TextPrimaryDark,
    surface = DeepNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = SlateCardSurface,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = SlateCardBorder
)

val EthiopianLightColorScheme = lightColorScheme(
    // 1. Blue (Primary Brand)
    primary = EthiopianBluePrimary,
    onPrimary = Color.White,
    primaryContainer = EthiopianBlueContainerLight,
    onPrimaryContainer = EthiopianBlueDark,

    // 2. Yellow (Secondary / Gold Rewards)
    secondary = EthiopianYellowPrimary,
    onSecondary = Color.Black,
    secondaryContainer = EthiopianYellowContainerLight,
    onSecondaryContainer = EthiopianYellowDark,

    // 3. Green (Tertiary / Verification & Growth)
    tertiary = EthiopianGreenPrimary,
    onTertiary = Color.White,
    tertiaryContainer = EthiopianGreenContainerLight,
    onTertiaryContainer = EthiopianGreenDark,

    // 4. Red (Error / Live Countdown / Urgent)
    error = EthiopianRedPrimary,
    onError = Color.White,
    errorContainer = EthiopianRedContainerLight,
    onErrorContainer = EthiopianRedDark,

    // Surfaces & Porcelain White Canvas
    background = PorcelainCanvas,
    onBackground = TextPrimaryLight,
    surface = PureWhiteSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = SoftGraySurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = SubtleBorderLight
)

@Composable
fun CheretaTelebirrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) EthiopianDarkColorScheme else EthiopianLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
