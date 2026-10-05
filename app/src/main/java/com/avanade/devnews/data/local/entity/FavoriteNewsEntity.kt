package com.avanade.devnews.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favorite_news",
    primaryKeys = ["userId", "articleUrl"]
)
data class FavoriteNewsEntity(
    val userId: String,
    val articleUrl: String,
    val sourceName: String,
    val author: String,
    val title: String,
    val description: String,
    val content: String,
    val imageUrl: String?,
    val publishedAt: String
)
