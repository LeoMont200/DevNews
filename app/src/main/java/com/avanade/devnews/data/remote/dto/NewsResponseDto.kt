package com.avanade.devnews.data.remote.dto

data class NewsResponseDto(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticleDto>
)

data class NewsArticleDto(
    val source: NewsSourceDto?,
    val author: String?,
    val title: String?,
    val description: String?,
    val url: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?
)

data class NewsSourceDto(
    val id: String?,
    val name: String?
)
