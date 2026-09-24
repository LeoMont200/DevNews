package com.avanade.devnews.domain.model

data class NewsPage(
    val articles: List<NewsArticle>,
    val totalResults: Int
)
