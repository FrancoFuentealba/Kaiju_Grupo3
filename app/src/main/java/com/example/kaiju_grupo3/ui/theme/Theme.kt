package com.example.kaiju_grupo3.ui.theme

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
    primary = KaijuPrimaryDark,
    onPrimary = KaijuOnPrimaryDark,
    primaryContainer = KaijuPrimaryContainerDark,
    onPrimaryContainer = KaijuOnPrimaryContainerDark,
    secondary = KaijuSecondaryDark,
    onSecondary = KaijuOnSecondaryDark,
    secondaryContainer = KaijuSecondaryContainerDark,
    onSecondaryContainer = KaijuOnSecondaryContainerDark,
    tertiary = KaijuTertiaryDark,
    onTertiary = KaijuOnTertiaryDark,
    tertiaryContainer = KaijuTertiaryContainerDark,
    onTertiaryContainer = KaijuOnTertiaryContainerDark,
    background = KaijuBackgroundDark,
    onBackground = KaijuOnBackgroundDark,
    surface = KaijuBackgroundDark,
    onSurface = KaijuOnBackgroundDark,
    surfaceVariant = KaijuSurfaceVariantDark,
    onSurfaceVariant = KaijuOnSurfaceVariantDark,
    outline = KaijuOutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = KaijuPrimary,
    onPrimary = KaijuOnPrimary,
    primaryContainer = KaijuPrimaryContainer,
    onPrimaryContainer = KaijuOnPrimaryContainer,
    secondary = KaijuSecondary,
    onSecondary = KaijuOnSecondary,
    secondaryContainer = KaijuSecondaryContainer,
    onSecondaryContainer = KaijuOnSecondaryContainer,
    tertiary = KaijuTertiary,
    onTertiary = KaijuOnTertiary,
    tertiaryContainer = KaijuTertiaryContainer,
    onTertiaryContainer = KaijuOnTertiaryContainer,
    background = KaijuBackground,
    onBackground = KaijuOnBackground,
    surface = KaijuBackground,
    onSurface = KaijuOnBackground,
    surfaceVariant = KaijuSurfaceVariant,
    onSurfaceVariant = KaijuOnSurfaceVariant,
    outline = KaijuOutline
)

@Composable
fun Kaiju_grupo3Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivado: con el color dinámico de Android 12+ se pierden los colores de marca de Kaiju.
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

