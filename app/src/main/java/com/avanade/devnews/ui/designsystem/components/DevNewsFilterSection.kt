package com.avanade.devnews.ui.designsystem.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens

@Composable
fun DevNewsFilterSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    categories: List<String> = listOf(
        "Lorem ipsum",
        "Lorem ipsum dolor",
        "Lorem ipsum amet",
        "Lorem ipsum elit",
        "Lorem ipsum tempor",
        "Lorem ipsum magna"
    )
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = DevNewsDesignTokens.cardShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Lorem ipsum dolor",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Lorem ipsum amet",
                style = MaterialTheme.typography.labelLarge
            )
            categories.chunked(size = 2).forEach { categoryRow ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    categoryRow.forEach { category ->
                        DevNewsCategoryChip(
                            label = category,
                            isSelected = category == selectedCategory,
                            onClick = { onCategorySelected(category) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            DevNewsPrimaryActionButton(
                text = "Lorem ipsum",
                onClick = {}
            )
        }
    }
}