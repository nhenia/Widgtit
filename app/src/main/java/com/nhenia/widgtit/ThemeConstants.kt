package com.nhenia.widgtit

import android.graphics.Color

data class WidgetTheme(
    val name: String,
    val textColor: Int,
    val backgroundColor: Int,
    val borderColor: Int
)

object ThemeConstants {
    val THEMES = listOf(
        WidgetTheme("Classic Dark", Color.WHITE, Color.parseColor("#333333"), Color.WHITE),
        WidgetTheme("Classic Light", Color.BLACK, Color.WHITE, Color.BLACK),
        WidgetTheme("High Contrast", Color.YELLOW, Color.BLACK, Color.YELLOW),
        WidgetTheme("Soft Sepia", Color.parseColor("#5F4B32"), Color.parseColor("#F4ECD8"), Color.parseColor("#5F4B32")),
        WidgetTheme("Ocean", Color.WHITE, Color.parseColor("#0077BE"), Color.WHITE),
        WidgetTheme("Forest", Color.WHITE, Color.parseColor("#228B22"), Color.WHITE),
        WidgetTheme("Retro", Color.parseColor("#39FF14"), Color.BLACK, Color.parseColor("#39FF14")),
        WidgetTheme("Vaporwave", Color.parseColor("#FF71CE"), Color.parseColor("#01CDFE"), Color.parseColor("#B967FF")),
        WidgetTheme("Zen", Color.parseColor("#4A4A4A"), Color.parseColor("#ECECEC"), Color.parseColor("#4A4A4A"))
    )

    val FONTS = listOf(
        "Default", "Serif", "Sans-Serif", "Monospace", "Cherry Bomb One",
        "sans-serif-light", "sans-serif-condensed", "sans-serif-medium", "sans-serif-black"
    )

    val BORDER_WIDTHS = listOf(1, 4, 8) // S, M, L in dp

    val BACKGROUND_IMAGES = listOf(
        "None", "Dots", "Lines", "Grid", "Stars"
    )
}
