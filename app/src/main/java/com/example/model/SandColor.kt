package com.example.model

import androidx.compose.ui.graphics.Color

enum class SandColor(
    val id: String,
    val displayName: String,
    val color: Color,
    val highlightColor: Color,
    val shadowColor: Color
) {
    RED(
        id = "red",
        displayName = "Crimson",
        color = Color(0xFFFF3366),
        highlightColor = Color(0xFFFF6B8B),
        shadowColor = Color(0xFFC2185B)
    ),
    YELLOW(
        id = "yellow",
        displayName = "Amber",
        color = Color(0xFFFFD000),
        highlightColor = Color(0xFFFFE066),
        shadowColor = Color(0xFFFF8F00)
    ),
    BLUE(
        id = "blue",
        displayName = "Cobalt",
        color = Color(0xFF2979FF),
        highlightColor = Color(0xFF69A1FF),
        shadowColor = Color(0xFF1565C0)
    ),
    GREEN(
        id = "green",
        displayName = "Emerald",
        color = Color(0xFF00E676),
        highlightColor = Color(0xFF66FFA6),
        shadowColor = Color(0xFF00A152)
    ),
    PURPLE(
        id = "purple",
        displayName = "Amethyst",
        color = Color(0xFFAA00FF),
        highlightColor = Color(0xFFD05CE3),
        shadowColor = Color(0xFF6A0080)
    ),
    ORANGE(
        id = "orange",
        displayName = "Coral",
        color = Color(0xFFFF6D00),
        highlightColor = Color(0xFFFF9E40),
        shadowColor = Color(0xFFC43E00)
    ),
    CYAN(
        id = "cyan",
        displayName = "Turquoise",
        color = Color(0xFF00E5FF),
        highlightColor = Color(0xFF6EFFFF),
        shadowColor = Color(0xFF00A0B2)
    ),
    PINK(
        id = "pink",
        displayName = "Rose",
        color = Color(0xFFFF4081),
        highlightColor = Color(0xFFFF79B0),
        shadowColor = Color(0xFFC60055)
    );

    companion object {
        fun fromId(id: String): SandColor? = entries.find { it.id == id }
    }
}
