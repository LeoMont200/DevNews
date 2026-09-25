package com.avanade.devnews.feature.news.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.domain.usecase.news.GetNewsPageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsListUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getNewsPageUseCase: GetNewsPageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20

    init {
        loadNextPage()
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingNextPage || state.endReached) {
            return
        }

        viewModelScope.launch {
            val isFirstPage = currentPage == 1
            _uiState.update {
                it.copy(
                    isLoading = isFirstPage,
                    isLoadingNextPage = !isFirstPage,
                    errorMessage = null
                )
            }

            val result = getNewsPageUseCase(page = currentPage, pageSize = pageSize)

            result.onSuccess { page ->
                val mergedArticles = (_uiState.value.articles + page.articles)
                    .distinctBy { article -> article.articleUrl }
                val endReached = page.articles.isEmpty() ||
                    mergedArticles.size >= page.totalResults ||
                    page.articles.size < pageSize

                currentPage += 1
                _uiState.value = NewsListUiState(
                    articles = mergedArticles,
                    isLoading = false,
                    isLoadingNextPage = false,
                    endReached = endReached
                )
            }.onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isLoadingNextPage = false,
                        errorMessage = exception.message ?: "Nao foi possivel carregar as noticias."
                    )
                }
            }
        }
    }

    fun retry() {
        loadNextPage()
    }
}
