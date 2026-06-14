package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val SophisticatedDarkColorScheme = darkColorScheme(
    primary = SophisticatedPrimary,
    onPrimary = SophisticatedOnPrimary,
    secondary = SophisticatedUserBubble,
    onSecondary = SophisticatedTextPrimary,
    tertiary = SophisticatedBadgeBg,
    onTertiary = SophisticatedBadgeText,
    background = SophisticatedBg,
    onBackground = SophisticatedTextPrimary,
    surface = SophisticatedCard,
    onSurface = SophisticatedTextPrimary,
    surfaceVariant = SophisticatedCard,
    onSurfaceVariant = SophisticatedTextPrimary,
    outline = SophisticatedBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for Sophisticated Dark
    dynamicColor: Boolean = false, // Disable dynamic system colors to preserve custom theme
    content: @Composable () -> Unit,
) {
    val colorScheme = SophisticatedDarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
