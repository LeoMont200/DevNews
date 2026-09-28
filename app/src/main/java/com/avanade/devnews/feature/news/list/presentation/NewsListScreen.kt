package com.avanade.devnews.feature.news.list.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.avanade.devnews.R
import com.avanade.devnews.core.notifications.PushNotificationManager
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleCard
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleUiModel
import com.avanade.devnews.ui.designsystem.components.DevNewsCategoryChip
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.components.DevNewsSearchField
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

private val categoryFilters = listOf(
    "Negócios",
    "Entretenimento",
    "Geral",
    "Saúde",
    "Ciência",
    "Esportes",
    "Tecnologia",
)

private val homeCategoryTabs = listOf("Todos") + categoryFilters

private enum class DateFilterOption {
    Recentes,
    Antigas,
    Personalizado
}

@Composable
fun NewsListScreen(
    modifier: Modifier = Modifier,
    onArticleClick: (NewsArticle) -> Unit,
    viewModel: NewsListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val spacing = DevNewsDesignTokens.spacing
    val context = LocalContext.current
    var isFilterDialogOpen by rememberSaveable { mutableStateOf(false) }
    var selectedDateFilter by rememberSaveable { mutableStateOf(DateFilterOption.Recentes) }

    fun onCategorySelected(category: String) {
        viewModel.applySearch("")
        viewModel.applyCategoryFilter(category)
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            val totalItemCount = listState.layoutInfo.totalItemsCount
            lastVisibleIndex to totalItemCount
        }
            .map { (lastVisibleIndex, totalItemCount) ->
                totalItemCount > 0 && lastVisibleIndex >= totalItemCount - 4
            }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                viewModel.loadNextPage()
            }
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        when {
            uiState.isLoading && uiState.articles.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null && uiState.articles.isEmpty() -> {
                NewsMessageState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    title = stringResource(R.string.news_error_title),
                    message = uiState.errorMessage.orEmpty(),
                    actionLabel = stringResource(R.string.news_retry_button),
                    onAction = viewModel::retry
                )
            }

            else -> {
                var headerHeightPx by remember { mutableIntStateOf(0) }
                val headerHeightDp = with(LocalDensity.current) { headerHeightPx.toDp() }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = spacing.medium,
                            top = headerHeightDp + spacing.small,
                            end = spacing.medium,
                            bottom = spacing.medium
                        ),
                        verticalArrangement = Arrangement.spacedBy(spacing.medium)
                    ) {
                        item {
                            Column(verticalArrangement = Arrangement.spacedBy(spacing.small)) {
                                Text(
                                    text = stringResource(R.string.news_list_title),
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Text(
                                    text = stringResource(R.string.news_list_subtitle),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
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

                        if (
                            uiState.displayedArticles.isEmpty() &&
                            (uiState.searchQuery.isNotBlank() || uiState.authorSearchQuery.isNotBlank())
                        ) {
                            item {
                                Text(
                                    text = stringResource(R.string.news_search_no_results),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        } else {
                            items(
                                items = uiState.displayedArticles,
                                key = { article -> article.articleUrl }
                            ) { article ->
                                DevNewsArticleCard(
                                    article = article.toUiModel(),
                                    onClick = { onArticleClick(article) }
                                )
                            }
                        }

                        if (uiState.isLoadingNextPage) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = spacing.medium),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        uiState.errorMessage?.takeIf { uiState.articles.isNotEmpty() }?.let { error ->
                            item {
                                NewsMessageState(
                                    title = stringResource(R.string.news_error_title),
                                    message = error,
                                    actionLabel = stringResource(R.string.news_retry_button),
                                    onAction = viewModel::retry
                                )
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .fillMaxWidth()
                            .zIndex(1f),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        NewsListFiltersHeader(
                            searchQuery = uiState.searchQuery,
                            onSearchQueryChange = viewModel::applySearch,
                            isFilterDialogOpen = isFilterDialogOpen,
                            selectedCategory = uiState.selectedCategory,
                            onFilterClick = { isFilterDialogOpen = true },
                            onCategorySelected = ::onCategorySelected,
                            modifier = Modifier
                                .fillMaxWidth()
                                .onGloballyPositioned { coordinates ->
                                    headerHeightPx = coordinates.size.height
                                }
                                .padding(horizontal = spacing.medium, vertical = spacing.small)
                        )
                    }
                }
            }
        }

        if (isFilterDialogOpen) {
            NewsFilterDialog(
                authorSearchQuery = uiState.authorSearchQuery,
                selectedDateFilter = selectedDateFilter,
                onAuthorSearchChange = viewModel::applyAuthorFilter,
                onDateFilterSelect = { selectedDateFilter = it },
                onClearFilters = {
                    viewModel.clearTextFilters()
                    viewModel.applyCategoryFilter("")
                    selectedDateFilter = DateFilterOption.Recentes
                },
                onDismiss = { isFilterDialogOpen = false }
            )
        }
    }
}

@Composable
private fun NewsListFiltersHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isFilterDialogOpen: Boolean,
    selectedCategory: String,
    onFilterClick: () -> Unit,
    onCategorySelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = DevNewsDesignTokens.spacing

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.small)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DevNewsSearchField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(0.78f),
                placeholder = stringResource(R.string.news_search_placeholder)
            )
            DevNewsCategoryChip(
                label = stringResource(R.string.news_filter_button),
                isSelected = isFilterDialogOpen,
                onClick = onFilterClick
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.small)
        ) {
            items(homeCategoryTabs) { category ->
                DevNewsCategoryChip(
                    label = category,
                    isSelected = if (category == "Todos") {
                        selectedCategory.isBlank()
                    } else {
                        selectedCategory == category
                    },
                    onClick = {
                        onCategorySelected(if (category == "Todos") "" else category)
                    }
                )
            }
        }
    }
}

@Composable
private fun NewsFilterDialog(
    authorSearchQuery: String,
    selectedDateFilter: DateFilterOption,
    onAuthorSearchChange: (String) -> Unit,
    onDateFilterSelect: (DateFilterOption) -> Unit,
    onClearFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    val spacing = DevNewsDesignTokens.spacing

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Scaffold(
                containerColor = MaterialTheme.colorScheme.background,
                bottomBar = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.medium)
                    ) {
                        DevNewsPrimaryActionButton(
                            text = stringResource(R.string.news_filter_apply_button),
                            onClick = onDismiss
                        )
                    }
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = spacing.medium, vertical = spacing.small),
                    verticalArrangement = Arrangement.spacedBy(spacing.medium),
                    contentPadding = PaddingValues(bottom = spacing.medium)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.news_filter_title),
                                style = MaterialTheme.typography.headlineSmall
                            )
                            TextButton(onClick = onClearFilters) {
                                Text(text = stringResource(R.string.news_filter_clear_all))
                            }
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.news_filter_author_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        DevNewsSearchField(
                            value = authorSearchQuery,
                            onValueChange = onAuthorSearchChange,
                            placeholder = stringResource(R.string.news_filter_author_placeholder)
                        )
                    }

                    item {
                        Text(
                            text = stringResource(R.string.news_filter_date_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    item {
                        DateFilterOptionItem(
                            label = stringResource(R.string.news_filter_date_recent),
                            isSelected = selectedDateFilter == DateFilterOption.Recentes,
                            onClick = { onDateFilterSelect(DateFilterOption.Recentes) }
                        )
                    }

                    item {
                        DateFilterOptionItem(
                            label = stringResource(R.string.news_filter_date_oldest),
                            isSelected = selectedDateFilter == DateFilterOption.Antigas,
                            onClick = { onDateFilterSelect(DateFilterOption.Antigas) }
                        )
                    }

                    item {
                        DateFilterOptionItem(
                            label = stringResource(R.string.news_filter_date_custom),
                            isSelected = selectedDateFilter == DateFilterOption.Personalizado,
                            onClick = { onDateFilterSelect(DateFilterOption.Personalizado) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DateFilterOptionItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun NewsMessageState(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val spacing = DevNewsDesignTokens.spacing

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(spacing.medium),
        verticalArrangement = Arrangement.spacedBy(spacing.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        DevNewsPrimaryActionButton(
            text = actionLabel,
            onClick = onAction
        )
    }
}

private fun NewsArticle.toUiModel(): DevNewsArticleUiModel {
    return DevNewsArticleUiModel(
        category = sourceName,
        title = title,
        summary = description.ifBlank { content.ifBlank { title } },
        publishInfo = formatPublishInfo(sourceName, publishedAt),
        imageUrl = imageUrl
    )
}

private fun formatPublishInfo(sourceName: String, publishedAt: String): String {
    val formattedDate = runCatching {
        OffsetDateTime.parse(publishedAt)
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.US))
    }.getOrDefault(publishedAt)

    return if (formattedDate.isBlank()) {
        sourceName
    } else {
        "$sourceName - $formattedDate"
    }
}
