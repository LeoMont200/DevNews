package com.avanade.devnews.data.mapper

import com.avanade.devnews.data.remote.dto.NewsArticleDto
import com.avanade.devnews.domain.model.NewsArticle

fun NewsArticleDto.toDomain(): NewsArticle? {
    val articleTitle = title?.takeIf { it.isNotBlank() } ?: return null
    val articleUrl = url?.takeIf { it.isNotBlank() } ?: return null

    return NewsArticle(
        sourceName = source?.name?.takeIf { it.isNotBlank() } ?: "NewsAPI",
        author = author.orEmpty(),
        title = articleTitle,
        description = description.orEmpty(),
        content = content.orEmpty(),
        articleUrl = articleUrl,
        imageUrl = urlToImage,
        publishedAt = publishedAt.orEmpty()
    )
}
