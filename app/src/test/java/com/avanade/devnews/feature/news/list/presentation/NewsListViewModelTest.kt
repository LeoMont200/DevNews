package com.avanade.devnews.feature.news.list.presentation

import com.avanade.devnews.FakeAuthRepository
import com.avanade.devnews.FakeFavoriteNewsDao
import com.avanade.devnews.FakeNewsRepository
import com.avanade.devnews.MainDispatcherRule
import com.avanade.devnews.domain.model.NewsPage
import com.avanade.devnews.domain.model.User
import com.avanade.devnews.domain.usecase.news.GetNewsPageUseCase
import com.avanade.devnews.sampleArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NewsListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads first page on init`() = runTest {
        val article = sampleArticle()
        val repository = FakeNewsRepository(
            mutableListOf(Result.success(NewsPage(listOf(article), totalResults = 10)))
        )

        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        assertEquals(listOf(article), viewModel.uiState.value.articles)
        assertEquals(listOf(article), viewModel.uiState.value.displayedArticles)
        assertEquals("news", repository.requests.single().query)
    }

    @Test
    fun `loads next page and deduplicates articles`() = runTest {
        val firstArticle = sampleArticle(articleUrl = "https://example.com/1")
        val duplicateArticle = sampleArticle(articleUrl = "https://example.com/1")
        val secondArticle = sampleArticle(articleUrl = "https://example.com/2", title = "Segundo")
        val repository = FakeNewsRepository(
            mutableListOf(
                Result.success(NewsPage(listOf(firstArticle), totalResults = 2)),
                Result.success(NewsPage(listOf(duplicateArticle, secondArticle), totalResults = 2))
            )
        )

        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.loadNextPage()
        advanceUntilIdle()

        assertEquals(listOf(firstArticle, secondArticle), viewModel.uiState.value.articles)
        assertTrue(viewModel.uiState.value.endReached)
    }

    @Test
    fun `exposes error when repository fails`() = runTest {
        val repository = FakeNewsRepository(
            mutableListOf(Result.failure(IllegalStateException("Falhou")))
        )

        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        assertEquals("Falhou", viewModel.uiState.value.errorMessage)
        assertTrue(viewModel.uiState.value.articles.isEmpty())
    }

    @Test
    fun `filters displayed articles by author`() = runTest {
        val firstArticle = sampleArticle(articleUrl = "https://example.com/1", author = "Ana")
        val secondArticle = sampleArticle(articleUrl = "https://example.com/2", author = "Bruno")
        val repository = FakeNewsRepository(
            mutableListOf(Result.success(NewsPage(listOf(firstArticle, secondArticle), totalResults = 2)))
        )

        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.applyAuthorFilter("ana")

        assertEquals(listOf(firstArticle), viewModel.uiState.value.displayedArticles)
    }

    @Test
    fun `applies category filter using mapped query`() = runTest {
        val repository = FakeNewsRepository(
            mutableListOf(
                Result.success(NewsPage(emptyList(), totalResults = 0)),
                Result.success(NewsPage(emptyList(), totalResults = 0))
            )
        )

        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.applyCategoryFilter("Tecnologia")
        advanceUntilIdle()

        assertEquals("Tecnologia", viewModel.uiState.value.selectedCategory)
        assertEquals("technology", repository.requests.last().query)
    }

    @Test
    fun `refreshes query after debounced search`() = runTest {
        val repository = FakeNewsRepository(
            mutableListOf(
                Result.success(NewsPage(emptyList(), totalResults = 0)),
                Result.success(NewsPage(emptyList(), totalResults = 0))
            )
        )

        val viewModel = buildViewModel(repository = repository)
        advanceUntilIdle()

        viewModel.applySearch("kotlin")
        advanceTimeBy(700)
        advanceUntilIdle()

        assertEquals("kotlin", viewModel.uiState.value.searchQuery)
        assertEquals("kotlin", repository.requests.last().query)
    }

    @Test
    fun `adds and removes favorite for logged user`() = runTest {
        val article = sampleArticle()
        val dao = FakeFavoriteNewsDao()
        val repository = FakeNewsRepository(
            mutableListOf(Result.success(NewsPage(listOf(article), totalResults = 1)))
        )
        val authRepository = FakeAuthRepository(
            User(id = "user-1", name = "User", email = "user@example.com")
        )
        val viewModel = NewsListViewModel(GetNewsPageUseCase(repository), dao, authRepository)
        advanceUntilIdle()

        viewModel.onFavoriteClick(article)
        advanceUntilIdle()
        assertTrue(article.articleUrl in viewModel.uiState.value.favoriteArticleUrls)

        viewModel.onFavoriteClick(article)
        advanceUntilIdle()
        assertTrue(article.articleUrl !in viewModel.uiState.value.favoriteArticleUrls)
    }

    @Test
    fun `cannot favorite without logged user`() = runTest {
        val repository = FakeNewsRepository(
            mutableListOf(Result.success(NewsPage(emptyList(), totalResults = 0)))
        )

        val viewModel = buildViewModel(repository = repository, authRepository = FakeAuthRepository(null))
        advanceUntilIdle()

        assertEquals(false, viewModel.canFavorite())
    }

    private fun buildViewModel(
        repository: FakeNewsRepository,
        dao: FakeFavoriteNewsDao = FakeFavoriteNewsDao(),
        authRepository: FakeAuthRepository = FakeAuthRepository(
            User(id = "user-1", name = "User", email = "user@example.com")
        )
    ): NewsListViewModel {
        return NewsListViewModel(
            getNewsPageUseCase = GetNewsPageUseCase(repository),
            favoriteNewsDao = dao,
            authRepository = authRepository
        )
    }
}
