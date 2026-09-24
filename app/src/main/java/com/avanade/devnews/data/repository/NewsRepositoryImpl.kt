package com.avanade.devnews.data.repository

import com.avanade.devnews.BuildConfig
import com.avanade.devnews.data.mapper.toDomain
import com.avanade.devnews.data.remote.api.NewsApiService
import com.avanade.devnews.domain.model.NewsPage
import com.avanade.devnews.domain.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService
) : NewsRepository {

    override suspend fun getNews(page: Int, pageSize: Int): Result<NewsPage> {
        if (BuildConfig.NEWS_API_KEY.isBlank()) {
            return Result.failure(IllegalStateException("News API key is not configured."))
        }

        return runCatching {
            val response = newsApiService.getEverything(
                apiKey = BuildConfig.NEWS_API_KEY,
                query = "technology",
                sortBy = "publishedAt",
                language = "en",
                page = page,
                pageSize = pageSize
            )

            if (response.status != "ok") {
                error("NewsAPI returned an invalid status: ${response.status}")
            }

            NewsPage(
                articles = response.articles.mapNotNull { it.toDomain() },
                totalResults = response.totalResults
            )
        }
    }
}
