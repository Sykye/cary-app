package com.szam.cary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val Primary = Color(0xFF2563EB)
val PrimaryLight = Color(0xFF60A5FA)
val PrimaryDark = Color(0xFF1D4ED8)
val Secondary = Color(0xFF60A5FA)
val Background = Color(0xFFF8FAFC)
val Surface = Color(0xFFFFFFFF)
val OnPrimary = Color(0xFFFFFFFF)
val OnBackground = Color(0xFF0F172A)
val OnSurface = Color(0xFF1E293B)
val Outline = Color(0xFFCBD5E1)
val SurfaceVariant = Color(0xFFEFF6FF)

// Status colors
val StatusHilang = Color(0xFFEF4444)
val StatusDitemukan = Color(0xFF10B981)
val StatusDalamKlaim = Color(0xFFF59E0B)
val StatusDikembalikan = Color(0xFF6366F1)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = Color(0xFFDBEAFE),
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEFF6FF),
    onSecondaryContainer = Color(0xFF1D4ED8),
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    outline = Outline,
    error = Color(0xFFEF4444)
)

@Composable
fun CaryTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color(0xFF2563EB).toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
