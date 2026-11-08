package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =========================================================================
// Ethiopian Inspired Palette (Green, Yellow, Red, and Blue)
// =========================================================================

// 1. Blue (ሰማያዊ) - Telebirr, Abbay (Blue Nile), Trust & Financial Integrity
val EthiopianBluePrimary = Color(0xFF0066CC) // Vivid Royal Telebirr Blue
val EthiopianBlueDark = Color(0xFF00458A)
val EthiopianBlueLight = Color(0xFF38BDF8) // Electric Sky Blue
val EthiopianBlueContainerLight = Color(0xFFE0F2FE)
val EthiopianBlueContainerDark = Color(0xFF0C4A6E)

// 2. Green (አረንጓዴ) - Fertility, Hope, Growth, Verified Transactions
val EthiopianGreenPrimary = Color(0xFF009B4D) // Official Ethiopian Emerald Green
val EthiopianGreenDark = Color(0xFF00753A)
val EthiopianGreenLight = Color(0xFF10B981) // Mint accent
val EthiopianGreenContainerLight = Color(0xFFD1FAE5)
val EthiopianGreenContainerDark = Color(0xFF064E3B)

// 3. Yellow (ቢጫ) - Adey Abeba, Radiant Prosperity, Gold Coins & Rewards
val EthiopianYellowPrimary = Color(0xFFFFB800) // Radiant Gold / Solar Amber
val EthiopianYellowDark = Color(0xFFD97706)
val EthiopianYellowLight = Color(0xFFFDE047)
val EthiopianYellowContainerLight = Color(0xFFFEF3C7)
val EthiopianYellowContainerDark = Color(0xFF78350F)

// 4. Red (ቀይ) - Strength, Urgency, Live Countdown, Alert
val EthiopianRedPrimary = Color(0xFFE02424) // Crimson Red
val EthiopianRedDark = Color(0xFF991B1B)
val EthiopianRedLight = Color(0xFFF87171)
val EthiopianRedContainerLight = Color(0xFFFEE2E2)
val EthiopianRedContainerDark = Color(0xFF7F1D1D)

// Surfaces & High-Contrast Backgrounds (ጥቁር / ንፁህ ነጭ)
val ObsidianMidnight = Color(0xFF0B0F19) // Rich Jet Midnight Black
val DeepNavySurface = Color(0xFF151D2E) // Surface container
val SlateCardSurface = Color(0xFF1E293B) // High contrast elevated card
val SlateCardBorder = Color(0xFF334155)

val PorcelainCanvas = Color(0xFFF8FAFC) // Crisp clean canvas
val PureWhiteSurface = Color(0xFFFFFFFF)
val SoftGraySurfaceVariant = Color(0xFFF1F5F9)
val SubtleBorderLight = Color(0xFFE2E8F0)

// Backward Compatibility Aliases for existing UI code
val TelebirrPrimary = EthiopianBluePrimary
val TelebirrPrimaryDark = EthiopianBlueDark
val TelebirrAccent = EthiopianBlueLight
val TelebirrGold = EthiopianYellowPrimary
val TelebirrGoldDark = EthiopianYellowDark
val TelebirrGreen = EthiopianGreenPrimary
val TelebirrGreenLight = EthiopianGreenLight
val TelebirrRed = EthiopianRedPrimary

val DarkBackground = ObsidianMidnight
val DarkSurface = DeepNavySurface
val DarkSurfaceVariant = SlateCardSurface
val DarkCardBorder = SlateCardBorder

val LightBackground = PorcelainCanvas
val LightSurface = PureWhiteSurface
val LightSurfaceVariant = SoftGraySurfaceVariant
val LightCardBorder = SubtleBorderLight

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFF94A3B8)
val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)

// Branded Gradients for Ethiopian Visual Identity
val EthiopianTricolorGradient = Brush.horizontalGradient(
    colors = listOf(EthiopianGreenPrimary, EthiopianYellowPrimary, EthiopianRedPrimary)
)

val EthiopianBrandGradient = Brush.linearGradient(
    colors = listOf(EthiopianBluePrimary, EthiopianGreenPrimary, EthiopianYellowPrimary)
)

val EthiopianHeroBannerGradient = Brush.linearGradient(
    colors = listOf(Color(0xFF003875), Color(0xFF0066CC), Color(0xFF0284C7))
)
