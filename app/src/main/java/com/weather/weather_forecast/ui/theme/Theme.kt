package com.weather.weather_forecast.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF2196F3),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = androidx.compose.ui.graphics.Color(0xFFBBDEFB),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1976D2),
    secondary = androidx.compose.ui.graphics.Color(0xFF03A9F4),
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFFB3E5FC),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF0288D1),
    background = androidx.compose.ui.graphics.Color(0xFFFAFAFA),
    onBackground = androidx.compose.ui.graphics.Color(0xFF212121),
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = androidx.compose.ui.graphics.Color(0xFF212121),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFFEEEEEE),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFF757575),
    error = androidx.compose.ui.graphics.Color(0xFFB00020),
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFFFCDD2),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFB00020)
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF0D47A1),
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF1976D2),
    onPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFBBDEFB),
    secondary = androidx.compose.ui.graphics.Color(0xFF81D4FA),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF01579B),
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF0288D1),
    onSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFB3E5FC),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color(0xFFFAFAFA),
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurface = androidx.compose.ui.graphics.Color(0xFFFAFAFA),
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2C2C2C),
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFBDBDBD),
    error = androidx.compose.ui.graphics.Color(0xFFCF6679),
    onError = androidx.compose.ui.graphics.Color.Black,
    errorContainer = androidx.compose.ui.graphics.Color(0xFFB00020),
    onErrorContainer = androidx.compose.ui.graphics.Color(0xFFFFCDD2)
)

@Composable
fun WeatherTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
