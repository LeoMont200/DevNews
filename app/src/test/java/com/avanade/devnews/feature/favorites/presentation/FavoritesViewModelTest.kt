package com.avanade.devnews.feature.favorites.presentation

import com.avanade.devnews.FakeAuthRepository
import com.avanade.devnews.FakeFavoriteNewsDao
import com.avanade.devnews.MainDispatcherRule
import com.avanade.devnews.data.mapper.toFavoriteEntity
import com.avanade.devnews.domain.model.User
import com.avanade.devnews.sampleArticle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `loads favorites for current user`() = runTest {
        val dao = FakeFavoriteNewsDao()
        val authRepository = FakeAuthRepository(
            User(id = "user-1", name = "User", email = "user@example.com")
        )
        val article = sampleArticle()
        dao.seed("user-1", listOf(article.toFavoriteEntity("user-1")))

        val viewModel = FavoritesViewModel(dao, authRepository)
        advanceUntilIdle()

        assertEquals(listOf(article), viewModel.uiState.value.articles)
    }

    @Test
    fun `removes favorite on click`() = runTest {
        val dao = FakeFavoriteNewsDao()
        val authRepository = FakeAuthRepository(
            User(id = "user-1", name = "User", email = "user@example.com")
        )
        val article = sampleArticle()
        dao.seed("user-1", listOf(article.toFavoriteEntity("user-1")))
        val viewModel = FavoritesViewModel(dao, authRepository)
        advanceUntilIdle()

        viewModel.onFavoriteClick(article)
        advanceUntilIdle()

        assertEquals(emptyList<com.avanade.devnews.domain.model.NewsArticle>(), viewModel.uiState.value.articles)
    }

    @Test
    fun `clears favorites when there is no logged user`() = runTest {
        val dao = FakeFavoriteNewsDao()
        val authRepository = FakeAuthRepository(null)

        val viewModel = FavoritesViewModel(dao, authRepository)
        advanceUntilIdle()

        assertEquals(emptyList<com.avanade.devnews.domain.model.NewsArticle>(), viewModel.uiState.value.articles)
    }
}
