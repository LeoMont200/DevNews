package com.avanade.devnews.domain.model

data class NewsArticle(
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val content: String,
    val articleUrl: String,
    val imageUrl: String?,
    val publishedAt: String
)
