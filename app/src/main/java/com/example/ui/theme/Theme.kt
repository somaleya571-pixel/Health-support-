package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MasterDarkColorScheme = darkColorScheme(
    primary = MasterRedPrimary,
    onPrimary = TextPrimaryDark,
    primaryContainer = MasterRedDark,
    onPrimaryContainer = TextPrimaryDark,
    secondary = MasterGold,
    onSecondary = MasterDarkBg,
    secondaryContainer = MasterCardBgDark,
    onSecondaryContainer = MasterGold,
    tertiary = HealthCyan,
    background = MasterDarkBg,
    onBackground = TextPrimaryDark,
    surface = MasterSurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = MasterCardBgDark,
    onSurfaceVariant = TextSecondaryDark,
    outline = MasterCardBorder
)

@Composable
fun HealthConsciousTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = MasterDarkBg.toArgb()
                it.navigationBarColor = MasterDarkBg.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = MasterDarkColorScheme,
        typography = Typography,
        content = content
    )
}
