package fr.apkfold.flare.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Dark = darkColorScheme(
    primary = Color(0xFFFF9A52),
    onPrimary = Color(0xFF2A1000),
    primaryContainer = Color(0xFF5A2600),
    onPrimaryContainer = Color(0xFFFFDBC7),
    secondary = Color(0xFFFFC56B),
    onSecondary = Color(0xFF2B1C00),
    secondaryContainer = Color(0xFF4A3410),
    onSecondaryContainer = Color(0xFFFFE2B5),
    tertiary = Color(0xFFFF7FA0),
    background = Color(0xFF140C0A),
    onBackground = Color(0xFFF2E3DC),
    surface = Color(0xFF140C0A),
    onSurface = Color(0xFFF2E3DC),
    surfaceVariant = Color(0xFF3A2C26),
    onSurfaceVariant = Color(0xFFD9C2B8),
    surfaceContainer = Color(0xFF221713),
    surfaceContainerHigh = Color(0xFF2C201B),
    surfaceContainerLow = Color(0xFF1C1310),
    error = Color(0xFFFFB4AB),
)

private val Light = lightColorScheme(
    primary = Color(0xFFC24A00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBC7),
    onPrimaryContainer = Color(0xFF3A1400),
    secondary = Color(0xFF8A5A00),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE2B5),
    onSecondaryContainer = Color(0xFF2B1C00),
    tertiary = Color(0xFFB0285A),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF231915),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF231915),
    surfaceVariant = Color(0xFFF4DED4),
    onSurfaceVariant = Color(0xFF52443C),
    surfaceContainer = Color(0xFFFCEAE2),
    surfaceContainerHigh = Color(0xFFF6E4DC),
    surfaceContainerLow = Color(0xFFFFF1EB),
)

@Composable
fun FlareTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (isSystemInDarkTheme()) Dark else Light, content = content)
}
