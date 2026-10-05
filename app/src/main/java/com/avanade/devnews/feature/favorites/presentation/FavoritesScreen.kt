package com.avanade.devnews.feature.favorites.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.avanade.devnews.R
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleCard
import com.avanade.devnews.ui.designsystem.components.DevNewsArticleUiModel
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    onArticleClick: (NewsArticle) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val spacing = DevNewsDesignTokens.spacing
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshCurrentUserBinding()
    }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.favorites_title)) }
            )
        }
    ) { innerPadding ->
        if (uiState.articles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(spacing.large),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.favorites_empty_state),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(spacing.medium),
                verticalArrangement = Arrangement.spacedBy(spacing.medium)
            ) {
                items(
                    items = uiState.articles,
                    key = { article -> article.articleUrl }
                ) { article ->
                    DevNewsArticleCard(
                        article = article.toUiModel(),
                        onClick = { onArticleClick(article) },
                        onFavoriteClick = { viewModel.onFavoriteClick(article) }
                    )
                }
            }
        }
    }
}

private fun NewsArticle.toUiModel(): DevNewsArticleUiModel {
    return DevNewsArticleUiModel(
        category = sourceName,
        title = title,
        summary = description.ifBlank { content.ifBlank { title } },
        publishInfo = formatPublishInfo(sourceName, publishedAt),
        imageUrl = imageUrl,
        isFavorite = true
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
