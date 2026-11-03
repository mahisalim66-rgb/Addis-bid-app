package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TelebirrAccent,
    onPrimary = Color.Black,
    primaryContainer = TelebirrPrimaryDark,
    onPrimaryContainer = Color.White,
    secondary = TelebirrGold,
    onSecondary = Color.Black,
    secondaryContainer = TelebirrGoldDark,
    onSecondaryContainer = Color.White,
    tertiary = TelebirrGreen,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = DarkCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = TelebirrPrimary,
    onPrimary = Color.White,
    primaryContainer = TelebirrAccent,
    onPrimaryContainer = Color.Black,
    secondary = TelebirrGold,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFFFF3CD),
    onSecondaryContainer = Color(0xFF664D03),
    tertiary = TelebirrGreen,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = LightCardBorder
)

@Composable
fun CheretaTelebirrTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // We intentionally deliver an ultra-sleek, immersive fintech theme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
