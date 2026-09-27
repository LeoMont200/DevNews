package com.avanade.devnews.feature.news.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.domain.usecase.news.FilterNewsByAuthorUseCase
import com.avanade.devnews.domain.usecase.news.FilterNewsByTitleUseCase
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
    val filteredArticles: List<NewsArticle> = emptyList(),
    val searchQuery: String = "",
    val authorSearchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingNextPage: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String = ""
)

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val getNewsPageUseCase: GetNewsPageUseCase,
    private val filterNewsByTitleUseCase: FilterNewsByTitleUseCase,
    private val filterNewsByAuthorUseCase: FilterNewsByAuthorUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20
    private var selectedQuery = DEFAULT_NEWS_QUERY

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

            val result = getNewsPageUseCase(
                page = currentPage,
                pageSize = pageSize,
                query = selectedQuery
            )

            result.onSuccess { page ->
                val currentSelectedCategory = _uiState.value.selectedCategory
                val currentSearchQuery = _uiState.value.searchQuery
                val currentAuthorSearchQuery = _uiState.value.authorSearchQuery
                val mergedArticles = (_uiState.value.articles + page.articles)
                    .distinctBy { article -> article.articleUrl }
                val filteredArticles = applyTextFilters(
                    articles = mergedArticles,
                    titleQuery = currentSearchQuery,
                    authorQuery = currentAuthorSearchQuery
                )
                val endReached = page.articles.isEmpty() ||
                    mergedArticles.size >= page.totalResults

                currentPage += 1
                _uiState.value = NewsListUiState(
                    articles = mergedArticles,
                    filteredArticles = filteredArticles,
                    searchQuery = currentSearchQuery,
                    authorSearchQuery = currentAuthorSearchQuery,
                    isLoading = false,
                    isLoadingNextPage = false,
                    endReached = endReached,
                    selectedCategory = currentSelectedCategory
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

    fun updateSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredArticles = applyTextFilters(
                    articles = state.articles,
                    titleQuery = query,
                    authorQuery = state.authorSearchQuery
                )
            )
        }
    }

    fun updateAuthorSearchQuery(query: String) {
        _uiState.update { state ->
            state.copy(
                authorSearchQuery = query,
                filteredArticles = applyTextFilters(
                    articles = state.articles,
                    titleQuery = state.searchQuery,
                    authorQuery = query
                )
            )
        }
    }

    fun applyCategoryFilter(category: String) {
        val nextQuery = categoryToQuery(category)
        _uiState.update { it.copy(selectedCategory = category) }

        if (nextQuery == selectedQuery) {
            return
        }

        selectedQuery = nextQuery
        currentPage = 1
        _uiState.value = NewsListUiState(
            selectedCategory = category,
            searchQuery = _uiState.value.searchQuery,
            authorSearchQuery = _uiState.value.authorSearchQuery
        )
        loadNextPage()
    }

    private fun applyTextFilters(
        articles: List<NewsArticle>,
        titleQuery: String,
        authorQuery: String
    ): List<NewsArticle> {
        val filteredByTitle = filterNewsByTitleUseCase(articles, titleQuery)
        return filterNewsByAuthorUseCase(filteredByTitle, authorQuery)
    }

    private fun categoryToQuery(category: String): String {
        return when (category) {
            "Negócios" -> "business"
            "Entretenimento" -> "entertainment"
            "Geral" -> "general"
            "Saúde" -> "health"
            "Ciência" -> "science"
            "Tecnologia" -> "technology"
            "Esportes" -> "sports"
            else -> DEFAULT_NEWS_QUERY
        }
    }

    private companion object {
        const val DEFAULT_NEWS_QUERY = "news"
    }
}
