package com.avanade.devnews.ui.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun DevNewsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    flavor: FlavorTheme = FlavorTheme.PROD,
    content: @Composable () -> Unit
) {
    val colors = getFlavorColors(flavor)
    
    val darkColorScheme = darkColorScheme(
        primary = colors.primary,
        onPrimary = colors.surfaceLight,
        secondary = colors.accent,
        onSecondary = colors.surfaceLight,
        tertiary = colors.primaryDark,
        background = colors.backgroundDark,
        onBackground = colors.textPrimaryDark,
        surface = colors.surfaceDark,
        onSurface = colors.textPrimaryDark,
        surfaceVariant = colors.surfaceVariantDark,
        onSurfaceVariant = colors.textSecondaryDark,
        outline = colors.borderDark
    )

    val lightColorScheme = lightColorScheme(
        primary = colors.primary,
        onPrimary = colors.surfaceLight,
        secondary = colors.accent,
        onSecondary = colors.surfaceLight,
        tertiary = colors.primaryDark,
        background = colors.backgroundLight,
        onBackground = colors.textPrimaryLight,
        surface = colors.surfaceLight,
        onSurface = colors.textPrimaryLight,
        surfaceVariant = colors.surfaceVariantLight,
        onSurfaceVariant = colors.textSecondaryLight,
        outline = colors.borderLight
    )

    val colorScheme = if (darkTheme) darkColorScheme else lightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}