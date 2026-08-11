package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val ElegantDarkColorScheme = darkColorScheme(
    primary = ElegantLavender,
    onPrimary = ElegantLavenderDark,
    primaryContainer = ElegantLavenderContainer,
    onPrimaryContainer = ElegantLavenderDeepest,
    secondary = ElegantRose,
    onSecondary = ElegantRoseDark,
    secondaryContainer = Color(0xFF5A3B47),
    onSecondaryContainer = Color(0xFFFFD8E4),
    tertiary = ElegantPurpleGrey,
    onTertiary = ElegantPurpleGreyDark,
    tertiaryContainer = Color(0xFF4A4458),
    onTertiaryContainer = Color(0xFFE8DEF8),
    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantDarkBorder,
    outlineVariant = ElegantDarkBorderSubtle
)

private val ElegantLightColorScheme = darkColorScheme( // For ADHD sensory clarity, Elegant Dark is the signature theme
    primary = ElegantLavender,
    onPrimary = ElegantLavenderDark,
    primaryContainer = ElegantLavenderContainer,
    onPrimaryContainer = ElegantLavenderDeepest,
    secondary = ElegantRose,
    onSecondary = ElegantRoseDark,
    tertiary = ElegantPurpleGrey,
    onTertiary = ElegantPurpleGreyDark,
    background = ElegantDarkBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantDarkSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantDarkSurfaceVariant,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantDarkBorder
)

@Composable
fun FocusFlowTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = ElegantDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    FocusFlowTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}


