package com.avanade.devnews.feature.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avanade.devnews.data.local.dao.FavoriteNewsDao
import com.avanade.devnews.data.mapper.toDomain
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val articles: List<NewsArticle> = emptyList()
)

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoriteNewsDao: FavoriteNewsDao,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private var currentUserId = ""
    private var observeFavoritesJob: Job? = null

    init {
        refreshCurrentUserBinding()
    }

    fun refreshCurrentUserBinding() {
        val nextUserId = authRepository.getCurrentUser()?.id.orEmpty()
        if (nextUserId == currentUserId && observeFavoritesJob != null) {
            return
        }

        currentUserId = nextUserId
        observeFavoritesJob?.cancel()

        if (currentUserId.isBlank()) {
            _uiState.value = FavoritesUiState(
                articles = emptyList()
            )
            return
        }

        observeFavorites(currentUserId)
    }

    fun onFavoriteClick(article: NewsArticle) {
        refreshCurrentUserBinding()
        if (currentUserId.isBlank()) return

        viewModelScope.launch {
            favoriteNewsDao.removeFavorite(
                userId = currentUserId,
                articleUrl = article.articleUrl
            )
        }
    }

    private fun observeFavorites(userId: String) {
        observeFavoritesJob = viewModelScope.launch {
            favoriteNewsDao.observeFavorites(userId).collectLatest { favorites ->
                _uiState.update {
                    it.copy(
                        articles = favorites.map { favorite -> favorite.toDomain() }
                    )
                }
            }
        }
    }
}
