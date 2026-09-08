package com.godark14.myhabit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AccentOrange,
    onPrimary = Color.White,
    secondary = AccentBrown,
    onSecondary = Color.White,
    background = BackgroundCream,
    onBackground = TextPrimary,
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = ReminderBannerBg,
    onSurfaceVariant = TextSecondary,
    outline = Divider,
    error = Color(0xFFD9534F)
)

@Composable
fun MyHabitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // App volontairement en un seul thème clair pour coller à la maquette,
    // darkTheme laissé en paramètre pour évolution future
    MaterialTheme(
        colorScheme = LightColors,
        typography = Typography,
        content = content
    )
}