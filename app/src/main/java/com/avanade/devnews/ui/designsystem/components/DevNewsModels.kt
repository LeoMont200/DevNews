package com.avanade.devnews.ui.designsystem.components

import androidx.compose.ui.graphics.vector.ImageVector

data class DevNewsArticleUiModel(
    val category: String,
    val title: String,
    val summary: String,
    val publishInfo: String,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)

data class DevNewsBottomNavItem(
    val label: String,
    val icon: ImageVector,
    val isSelected: Boolean
)