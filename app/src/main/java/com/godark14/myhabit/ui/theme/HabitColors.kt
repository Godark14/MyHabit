package com.godark14.myhabit.ui.theme

import androidx.compose.ui.graphics.Color

object HabitColors {
    val palette = listOf(
        "#5A3728" to Color(0xFF5A3728), // marron
        "#B5622E" to Color(0xFFB5622E), // cuivre
        "#8A9A4E" to Color(0xFF8A9A4E), // olive
        "#E8A0C4" to Color(0xFFE8A0C4), // rose
        "#4A7A8C" to Color(0xFF4A7A8C), // bleu ardoise
        "#C4574A" to Color(0xFFC4574A), // rouge brique
        "#7A5FA0" to Color(0xFF7A5FA0), // violet
        "#4A8C6F" to Color(0xFF4A8C6F)  // vert forêt
    )

    fun fromHex(hex: String): Color =
        palette.firstOrNull { it.first == hex }?.second ?: palette.first().second
}