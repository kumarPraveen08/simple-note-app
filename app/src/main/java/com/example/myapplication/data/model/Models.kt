package com.example.myapplication.data.model

enum class NoteSort {
    DATE_NEWEST,
    DATE_OLDEST,
    TITLE_AZ,
    TITLE_ZA
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppThemeStyle {
    PURPLE,
    ROSE,
    BLUE,
    TEAL,
    GREEN,
    AMBER,
    ORCHID,
    MAUVE
}

enum class NoteFilter {
    ALL,
    PINNED,
    FOLDER,
    ARCHIVE,
    TRASH
}

data class ChecklistItem(
    val id: String,
    val text: String,
    val checked: Boolean = false
)

object NoteColors {
    val options = listOf(
        0x00000000L, // default / none
        0xFFFFF8E1L, // amber
        0xFFE8F5E9L, // green
        0xFFE3F2FDL, // blue
        0xFFFCE4ECL, // pink
        0xFFF3E5F5L, // purple
        0xFFFFEBEEL, // red soft
        0xFFE0F7FAL  // cyan
    )
}
