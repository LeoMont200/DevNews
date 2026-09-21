package com.avanade.devnews.ui.designsystem.showcase

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.avanade.devnews.R
import com.avanade.devnews.core.notifications.PushNotificationManager
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleCard
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleUiModel
import com.avanade.devnews.ui.designsystem.components.DevNewsBottomNavItem
import com.avanade.devnews.ui.designsystem.components.DevNewsBottomNavigationBar
import com.avanade.devnews.ui.designsystem.components.DevNewsCategoryChip
import com.avanade.devnews.ui.designsystem.components.DevNewsFilterSection
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.components.DevNewsSearchField
import com.avanade.devnews.ui.designsystem.theme.DevNewsTheme
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens

private val showcaseCategories = listOf(
    "Lorem ipsum",
    "Lorem ipsum dolor",
    "Lorem ipsum amet",
    "Lorem ipsum elit",
    "Lorem ipsum tempor"
)

private val showcaseArticles = listOf(
    DevNewsArticleUiModel(
        category = "Lorem ipsum",
        title = "Lorem ipsum dolor sit amet consectetur adipiscing elit",
        summary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore.",
        publishInfo = "Lorem ipsum dolor"
    ),
    DevNewsArticleUiModel(
        category = "Lorem ipsum dolor",
        title = "Lorem ipsum amet consectetur adipiscing elit sed do",
        summary = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut magna.",
        publishInfo = "Lorem ipsum amet"
    )
)

@Composable
fun DesignSystemShowcaseScreen(modifier: Modifier = Modifier) {
    val spacing = DevNewsDesignTokens.spacing
    val context = LocalContext.current
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf("Lorem ipsum") }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            DevNewsBottomNavigationBar(
                items = listOf(
                    DevNewsBottomNavItem(label = "Lorem ipsum", isSelected = selectedTab == 0),
                    DevNewsBottomNavItem(label = "Lorem amet", isSelected = selectedTab == 1),
                    DevNewsBottomNavItem(label = "Lorem elit", isSelected = selectedTab == 2),
                    DevNewsBottomNavItem(label = "Lorem tempor", isSelected = selectedTab == 3)
                ),
                onItemClick = { selectedTab = it }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                    Text(
                        text = "Lorem ipsum dolor",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    DevNewsSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = "Lorem ipsum dolor sit amet"
                    )
                    DevNewsPrimaryActionButton(
                        text = stringResource(R.string.push_notification_test_button),
                        onClick = {
                            PushNotificationManager.showNotification(
                                context = context,
                                title = context.getString(R.string.push_notification_test_title),
                                body = context.getString(R.string.push_notification_test_body)
                            )
                        }
                    )
                }
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(spacing.small)) {
                    items(items = showcaseCategories, key = { it }) { category ->
                        DevNewsCategoryChip(
                            label = category,
                            isSelected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }
            }

            itemsIndexed(
                items = showcaseArticles,
                key = { _, article -> "${article.category}-${article.title}" }
            ) { _, article ->
                DevNewsArticleCard(article = article)
            }

            item {
                DevNewsFilterSection(
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DesignSystemShowcaseScreenPreview() {
    DevNewsTheme {
        DesignSystemShowcaseScreen()
    }
}
