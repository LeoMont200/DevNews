package com.avanade.devnews.domain.repository

import com.avanade.devnews.domain.model.NewsPage

interface NewsRepository {
    suspend fun getNews(page: Int, pageSize: Int): Result<NewsPage>
}
