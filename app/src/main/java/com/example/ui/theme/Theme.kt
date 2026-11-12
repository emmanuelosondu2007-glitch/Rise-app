package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RiseDarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = NavyDarkest,
    primaryContainer = NavySurfaceElevated,
    onPrimaryContainer = GoldBright,
    secondary = GoldSecondary,
    onSecondary = NavyDarkest,
    secondaryContainer = NavySurface,
    onSecondaryContainer = GoldBright,
    tertiary = FlameOrange,
    onTertiary = NavyDarkest,
    tertiaryContainer = NavySurfaceElevated,
    onTertiaryContainer = FlameYellow,
    background = NavyBackground,
    onBackground = TextWhite,
    surface = NavySurface,
    onSurface = TextWhite,
    surfaceVariant = NavySurfaceElevated,
    onSurfaceVariant = TextSilver,
    outline = NavySurfaceBorder,
    outlineVariant = NavySurfaceHighlight
)

@Composable
fun RiseTheme(
    darkTheme: Boolean = true, // Default to premium dark as requested
    content: @Composable () -> Unit
) {
    val colorScheme = RiseDarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = NavyDarkest.toArgb()
                it.navigationBarColor = NavyDarkest.toArgb()
                val insetsController = WindowCompat.getInsetsController(it, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Backwards compatibility alias
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    RiseTheme(darkTheme = darkTheme, content = content)
}
