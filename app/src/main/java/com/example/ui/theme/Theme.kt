package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = N8nPrimary, 
    secondary = N8nSecondary, 
    background = N8nBackgroundDark,
    surface = N8nSurfaceDark,
    onPrimary = N8nTextDark,
    onBackground = N8nTextDark,
    onSurface = N8nTextDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = N8nPrimary,
    secondary = N8nSecondary,
    background = N8nBackgroundLight,
    surface = N8nSurfaceLight,
    onPrimary = N8nTextLight,
    onBackground = N8nTextLight,
    onSurface = N8nTextLight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Dynamic color is available on Android 12+
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
