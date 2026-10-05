package com.avanade.devnews.feature.news.detail.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.avanade.devnews.R
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.ui.designsystem.components.DevNewsPrimaryActionButton
import com.avanade.devnews.ui.designsystem.tokens.DevNewsDesignTokens
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NewsDetailScreen(
    article: NewsArticle,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onOpenWebView: (String) -> Unit = {}
) {
    val spacing = DevNewsDesignTokens.spacing

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(spacing.medium),
            verticalArrangement = Arrangement.spacedBy(spacing.medium)
        ) {
            DevNewsPrimaryActionButton(
                text = stringResource(R.string.news_back_button),
                onClick = onBack
            )
            
            article.imageUrl?.takeIf { it.isNotBlank() }?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = article.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
            
            Text(
                text = article.sourceName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = article.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = buildMetadata(article),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            article.description.takeIf { it.isNotBlank() }?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = Int.MAX_VALUE
                )
            }
            article.content.takeIf { it.isNotBlank() }?.let { content ->
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = Int.MAX_VALUE
                )
            }
            DevNewsPrimaryActionButton(
                text = stringResource(R.string.news_open_full_article_button),
                modifier = Modifier.fillMaxWidth(),
                onClick = { onOpenWebView(article.articleUrl) }
            )
        }
    }
}

private fun buildMetadata(article: NewsArticle): String {
    val author = article.author.takeIf { it.isNotBlank() } ?: "Autor nao informado"
    val publishedAt = runCatching {
        OffsetDateTime.parse(article.publishedAt)
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.US))
    }.getOrDefault(article.publishedAt)

    return if (publishedAt.isBlank()) {
        author
    } else {
        "$author - $publishedAt"
    }
}
