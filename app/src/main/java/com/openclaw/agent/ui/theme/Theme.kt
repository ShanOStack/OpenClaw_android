package com.openclaw.agent.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    secondary = ElectricViolet,
    background = DeepCharcoal,
    surface = CardDark,
    onPrimary = DeepCharcoal,
    onSecondary = White,
    onBackground = White,
    onSurface = White
)

@Composable
fun OpenClawTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
