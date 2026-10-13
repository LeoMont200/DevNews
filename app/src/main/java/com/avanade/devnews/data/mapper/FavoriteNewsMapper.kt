package com.avanade.devnews.data.mapper

import com.avanade.devnews.data.local.entity.FavoriteNewsEntity
import com.avanade.devnews.domain.model.NewsArticle

fun NewsArticle.toFavoriteEntity(userId: String): FavoriteNewsEntity {
    return FavoriteNewsEntity(
        userId = userId,
        articleUrl = articleUrl,
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        content = content,
        imageUrl = imageUrl,
        publishedAt = publishedAt
    )
}

fun FavoriteNewsEntity.toDomain(): NewsArticle {
    return NewsArticle(
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        content = content,
        articleUrl = articleUrl,
        imageUrl = imageUrl,
        publishedAt = publishedAt
    )
}
