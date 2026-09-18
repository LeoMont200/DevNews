package com.avanade.devnews.ui.designsystem.theme

import androidx.compose.ui.graphics.Color

enum class FlavorTheme {
    PROD,
    DEV,
    HML;

    companion object {
        fun fromBuildFlavor(flavor: String): FlavorTheme = when (flavor.lowercase()) {
            "dev" -> DEV
            "hml" -> HML
            else -> PROD
        }
    }
}

data class FlavorColors(
    val primary: Color,
    val primaryDark: Color,
    val accent: Color,
    val accentDark: Color,
    val backgroundLight: Color,
    val surfaceLight: Color,
    val surfaceVariantLight: Color,
    val borderLight: Color,
    val textPrimaryLight: Color,
    val textSecondaryLight: Color,
    val backgroundDark: Color,
    val surfaceDark: Color,
    val surfaceVariantDark: Color,
    val borderDark: Color,
    val textPrimaryDark: Color,
    val textSecondaryDark: Color
)

val ProdColors = FlavorColors(
    primary = Color(0xFFFF7A00),
    primaryDark = Color(0xFFFF5E00),
    accent = Color(0xFF8A0063),
    accentDark = Color(0xFF640045),
    backgroundLight = Color(0xFFF7F8FC),
    surfaceLight = Color(0xFFFFFFFF),
    surfaceVariantLight = Color(0xFFF2F3F7),
    borderLight = Color(0xFFDADCE5),
    textPrimaryLight = Color(0xFF171A22),
    textSecondaryLight = Color(0xFF666B7A),
    backgroundDark = Color(0xFF0E1016),
    surfaceDark = Color(0xFF181C25),
    surfaceVariantDark = Color(0xFF232938),
    borderDark = Color(0xFF353E52),
    textPrimaryDark = Color(0xFFF1F3FA),
    textSecondaryDark = Color(0xFFB5BDD2)
)

val DevColors = FlavorColors(
    primary = Color(0xFF0066FF),
    primaryDark = Color(0xFF0050CC),
    accent = Color(0xFF0099FF),
    accentDark = Color(0xFF0077CC),
    backgroundLight = Color(0xFFF0F5FF),
    surfaceLight = Color(0xFFFFFFFF),
    surfaceVariantLight = Color(0xFFE6F0FF),
    borderLight = Color(0xFFB3D9FF),
    textPrimaryLight = Color(0xFF001A4D),
    textSecondaryLight = Color(0xFF335580),
    backgroundDark = Color(0xFF0A1A33),
    surfaceDark = Color(0xFF1A2E4D),
    surfaceVariantDark = Color(0xFF2D4666),
    borderDark = Color(0xFF4D6680),
    textPrimaryDark = Color(0xFFE6F0FF),
    textSecondaryDark = Color(0xFFB3D9FF)
)

val HmlColors = FlavorColors(
    primary = Color(0xFF00CC66),
    primaryDark = Color(0xFF00AA55),
    accent = Color(0xFF00DD77),
    accentDark = Color(0xFF00BB66),
    backgroundLight = Color(0xFFF0FFF5),
    surfaceLight = Color(0xFFFFFFFF),
    surfaceVariantLight = Color(0xFFE6FFE6),
    borderLight = Color(0xFFB3E6B3),
    textPrimaryLight = Color(0xFF004D1A),
    textSecondaryLight = Color(0xFF338033),
    backgroundDark = Color(0xFF0A330A),
    surfaceDark = Color(0xFF1A4D1A),
    surfaceVariantDark = Color(0xFF2D662D),
    borderDark = Color(0xFF4D804D),
    textPrimaryDark = Color(0xFFE6FFE6),
    textSecondaryDark = Color(0xFFB3E6B3)
)

fun getFlavorColors(flavor: FlavorTheme): FlavorColors = when (flavor) {
    FlavorTheme.PROD -> ProdColors
    FlavorTheme.DEV -> DevColors
    FlavorTheme.HML -> HmlColors
}
