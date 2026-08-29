package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Master Dark Canvas Colors
val MasterDarkBg = Color(0xFF090C10)
val MasterSurfaceDark = Color(0xFF131822)
val MasterCardBgDark = Color(0xFF1B212D)
val MasterCardBorder = Color(0x33FF2A2A)

// Master Athletic Red Accents (Matching Embroidered Red Horse Brand)
val MasterRedPrimary = Color(0xFFFF2A2A)
val MasterRedDark = Color(0xFFD50000)
val MasterRedLight = Color(0xFFFF5252)
val MasterRedGlow = Color(0x66FF2A2A)

// Master Luxury Gold / Amber
val MasterGold = Color(0xFFFFD700)
val MasterAmber = Color(0xFFFFB300)

// Master Functional Health Accents
val HealthGreen = Color(0xFF00E676)
val HealthCyan = Color(0xFF00E5FF)
val HealthPurple = Color(0xFFB388FF)
val HealthOrange = Color(0xFFFF9100)

// Text and Subtle Grays
val TextPrimaryDark = Color(0xFFF0F6FC)
val TextSecondaryDark = Color(0xFF8B949E)
val TextMutedDark = Color(0xFF484F58)

// Master Gradients
val MasterCardGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFF221518),
        Color(0xFF171B26),
        Color(0xFF10131A)
    )
)

val MasterRedGradient = Brush.horizontalGradient(
    colors = listOf(MasterRedPrimary, MasterRedDark)
)

val MasterGoldGradient = Brush.horizontalGradient(
    colors = listOf(MasterGold, MasterAmber)
)

val MasterRingGradient = Brush.sweepGradient(
    colors = listOf(
        MasterRedPrimary,
        MasterGold,
        HealthGreen,
        HealthCyan,
        MasterRedPrimary
    )
)
