package com.example.fintechspender.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = AccentPink,
    background = DarkBackground,
    surface = LightSurface,
    onPrimary = LightTextColor,
    onSecondary = LightTextColor,
    onTertiary = LightTextColor,
    onBackground = DarkTextColor,
    onSurface = DarkTextColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    secondary = SecondaryGreen,
    tertiary = AccentPink,
    background = LightSurface, // Using a light gray for the background
    surface = DarkBackground,      // And a white for surfaces
    onPrimary = LightTextColor,
    onSecondary = LightTextColor,
    onTertiary = LightTextColor,
    onBackground = DarkTextColor,
    onSurface = DarkTextColor
)

@Composable
fun FintechspenderTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
