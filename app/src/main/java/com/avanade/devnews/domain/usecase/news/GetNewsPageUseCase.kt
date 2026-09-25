package com.avanade.devnews.domain.usecase.news

import com.avanade.devnews.domain.model.NewsPage
import com.avanade.devnews.domain.repository.NewsRepository
import javax.inject.Inject

class GetNewsPageUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(page: Int, pageSize: Int): Result<NewsPage> {
        return repository.getNews(page = page, pageSize = pageSize)
    }
}
