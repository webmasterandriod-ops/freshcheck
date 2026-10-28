package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FreshKitchenColorScheme = lightColorScheme(
    primary = LeafGreen,
    onPrimary = FreshCardSurface,
    primaryContainer = LeafGreenSoft,
    onPrimaryContainer = LeafGreenDark,
    secondary = WarmAmber,
    onSecondary = FreshInk,
    secondaryContainer = WarmAmberSoft,
    onSecondaryContainer = WarmAmberDark,
    tertiary = UrgentRed,
    onTertiary = FreshCardSurface,
    tertiaryContainer = UrgentRedSoft,
    onTertiaryContainer = UrgentRedDark,
    background = FreshBackground,
    onBackground = FreshInk,
    surface = FreshCardSurface,
    onSurface = FreshInk,
    surfaceVariant = FreshSurfaceSubtle,
    onSurfaceVariant = FreshMutedText,
    outline = FreshBorderSubtle,
    error = UrgentRed,
    onError = FreshCardSurface,
    errorContainer = UrgentRedSoft,
    onErrorContainer = UrgentRedDark
)

@Composable
fun FreshCheckTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FreshKitchenColorScheme,
        typography = Typography,
        content = content
    )
}
