package com.avanade.devnews.ui.designsystem.components

data class DevNewsArticleUiModel(
    val category: String,
    val title: String,
    val summary: String,
    val publishInfo: String
)

data class DevNewsBottomNavItem(
    val label: String,
    val isSelected: Boolean
)