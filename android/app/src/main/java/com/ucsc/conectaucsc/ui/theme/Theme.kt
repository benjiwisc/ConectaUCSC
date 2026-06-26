package com.ucsc.conectaucsc.ui.theme

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
    primary = UcscRedDark,
    onPrimary = UcscRedOnDark,
    primaryContainer = UcscRedContainerDark,
    onPrimaryContainer = UcscRedOnContainerDark,
    secondary = UcscNavyDark,
    onSecondary = UcscNavyOnDark,
    secondaryContainer = UcscNavyContainerDark,
    onSecondaryContainer = UcscNavyOnContainerDark,
    tertiary = UcscGoldDark,
    onTertiary = UcscGoldOnDark,
    tertiaryContainer = UcscGoldContainerDark,
    onTertiaryContainer = UcscGoldOnContainerDark,
    background = NeutralDarkBackground,
    onBackground = NeutralDarkOnBackground,
    surface = NeutralDarkSurface,
    onSurface = NeutralDarkOnSurface,
    surfaceVariant = NeutralDarkSurfaceVariant,
    onSurfaceVariant = NeutralDarkOnSurfaceVariant,
    outline = NeutralDarkOutline
)

private val LightColorScheme = lightColorScheme(
    primary = UcscRedLight,
    onPrimary = UcscRedOnLight,
    primaryContainer = UcscRedContainerLight,
    onPrimaryContainer = UcscRedOnContainerLight,
    secondary = UcscNavyLight,
    onSecondary = UcscNavyOnLight,
    secondaryContainer = UcscNavyContainerLight,
    onSecondaryContainer = UcscNavyOnContainerLight,
    tertiary = UcscGoldLight,
    onTertiary = UcscGoldOnLight,
    tertiaryContainer = UcscGoldContainerLight,
    onTertiaryContainer = UcscGoldOnContainerLight,
    background = NeutralLightBackground,
    onBackground = NeutralLightOnBackground,
    surface = NeutralLightSurface,
    onSurface = NeutralLightOnSurface,
    surfaceVariant = NeutralLightSurfaceVariant,
    onSurfaceVariant = NeutralLightOnSurfaceVariant,
    outline = NeutralLightOutline
)

@Composable
fun ConectaUCSCTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    
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