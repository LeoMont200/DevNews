package com.avanade.devnews.feature.news.list.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.avanade.devnews.R
import com.avanade.devnews.core.notifications.PushNotificationManager
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleCard
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleUiModel
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

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
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(spacing.medium),
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

                    items(
                        items = uiState.articles,
                        key = { article -> article.articleUrl }
                    ) { article ->
                        DevNewsArticleCard(
                            article = article.toUiModel(),
                            onClick = { onArticleClick(article) }
                        )
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
            }
        }
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
