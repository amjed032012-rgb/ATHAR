package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = SaroojBrownNight,
    onPrimary = TextPrimaryDark,
    primaryContainer = SaroojBrownDark,
    onPrimaryContainer = SaroojContainer,
    secondary = OasisGreenLight,
    onSecondary = SandSurfaceNight,
    secondaryContainer = OasisGreen,
    onSecondaryContainer = OasisGreenContainer,
    tertiary = MihrabGoldLight,
    onTertiary = SandSurfaceNight,
    background = SandBackgroundNight,
    onBackground = TextPrimaryLight,
    surface = SandSurfaceNight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SandSurfaceVariantNight,
    onSurfaceVariant = TextSecondaryLight,
    outline = SandOutline,
    outlineVariant = SandOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = SaroojBrown,
    onPrimary = SandSurfaceLight,
    primaryContainer = SaroojContainer,
    onPrimaryContainer = OnSaroojContainer,
    secondary = OasisGreen,
    onSecondary = SandSurfaceLight,
    secondaryContainer = OasisGreenContainer,
    onSecondaryContainer = OnOasisGreenContainer,
    tertiary = MihrabGold,
    onTertiary = SandSurfaceLight,
    tertiaryContainer = MihrabGoldContainer,
    onTertiaryContainer = OnMihrabGoldContainer,
    background = SandBackgroundLight,
    onBackground = TextPrimaryDark,
    surface = SandSurfaceLight,
    onSurface = TextPrimaryDark,
    surfaceVariant = SandSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = SandOutline,
    outlineVariant = SandOutlineVariant
)

@Composable
fun AtharTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
