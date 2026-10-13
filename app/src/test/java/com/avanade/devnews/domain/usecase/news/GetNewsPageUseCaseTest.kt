package com.avanade.devnews.domain.usecase.news

import com.avanade.devnews.FakeNewsRepository
import com.avanade.devnews.domain.model.NewsPage
import com.avanade.devnews.sampleArticle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetNewsPageUseCaseTest {

    @Test
    fun `delegates request to repository`() = runTest {
        val expectedPage = NewsPage(
            articles = listOf(sampleArticle()),
            totalResults = 1
        )
        val repository = FakeNewsRepository(
            mutableListOf(Result.success(expectedPage))
        )
        val useCase = GetNewsPageUseCase(repository)

        val result = useCase(page = 2, pageSize = 15, query = "android")

        assertEquals(Result.success(expectedPage), result)
        assertEquals(1, repository.requests.size)
        assertEquals(2, repository.requests.single().page)
        assertEquals(15, repository.requests.single().pageSize)
        assertEquals("android", repository.requests.single().query)
    }
}
