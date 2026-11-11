package com.warrantyshelf.notes.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightScheme = lightColorScheme(
    primary = PetrolPrimary,
    onPrimary = OnDark,
    primaryContainer = ShelfSurfaceMuted,
    onPrimaryContainer = PetrolDeep,
    secondary = OrangeAccent,
    onSecondary = OnDark,
    secondaryContainer = OrangeSoft,
    onSecondaryContainer = PetrolDeep,
    tertiary = SageCovered,
    onTertiary = OnDark,
    background = ShelfBackground,
    onBackground = TextPrimary,
    surface = ShelfSurface,
    onSurface = TextPrimary,
    surfaceVariant = ShelfSurfaceMuted,
    onSurfaceVariant = TextSecondary,
    outline = ShelfLine,
    outlineVariant = ShelfLine,
    error = ClayExpired,
    onError = OnDark
)

private val DarkScheme = darkColorScheme(
    primary = PetrolPrimary,
    onPrimary = OnDark,
    primaryContainer = PetrolDeep,
    onPrimaryContainer = OnDark,
    secondary = OrangeAccent,
    onSecondary = OnDark,
    secondaryContainer = PetrolDeep,
    onSecondaryContainer = OrangeSoft,
    tertiary = SageCovered,
    onTertiary = OnDark,
    background = TextPrimary,
    onBackground = OnDark,
    surface = PetrolDeep,
    onSurface = OnDark,
    surfaceVariant = PetrolDeep,
    onSurfaceVariant = ShelfLine,
    outline = PetrolPrimary,
    outlineVariant = PetrolPrimary,
    error = ClayExpired,
    onError = OnDark
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        typography = ShelfTypography,
        content = content
    )
}
