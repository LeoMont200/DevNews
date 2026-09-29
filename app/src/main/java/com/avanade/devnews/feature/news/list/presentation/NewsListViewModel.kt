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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsListUiState(
    val articles: List<NewsArticle> = emptyList(),
    val displayedArticles: List<NewsArticle> = emptyList(),
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
    private val getNewsPageUseCase: GetNewsPageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsListUiState())
    val uiState: StateFlow<NewsListUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val pageSize = 20
    private var selectedQuery = DEFAULT_NEWS_QUERY
    private val debouncedSearchQuery = MutableStateFlow("")

    init {
        observeDebouncedTextFilters()
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
                val displayedArticles = applyAuthorFilterLocally(
                    articles = mergedArticles,
                    authorSearchQuery = currentAuthorSearchQuery
                )
                val endReached = page.articles.isEmpty() ||
                    mergedArticles.size >= page.totalResults

                currentPage += 1
                _uiState.value = NewsListUiState(
                    articles = mergedArticles,
                    displayedArticles = displayedArticles,
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

    fun applySearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        debouncedSearchQuery.value = query
    }

    fun applyAuthorFilter(query: String) {
        _uiState.update { state ->
            state.copy(
                authorSearchQuery = query,
                displayedArticles = applyAuthorFilterLocally(
                    articles = state.articles,
                    authorSearchQuery = query
                )
            )
        }
    }

    fun clearTextFilters() {
        _uiState.update { state ->
            state.copy(
                searchQuery = "",
                authorSearchQuery = "",
                displayedArticles = state.articles
            )
        }
        debouncedSearchQuery.value = ""
        refreshWithCurrentFilters()
    }

    fun applyCategoryFilter(category: String) {
        val nextQuery = buildApiQuery(
            category = category,
            searchQuery = _uiState.value.searchQuery
        )
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

    private fun observeDebouncedTextFilters() {
        viewModelScope.launch {
            debouncedSearchQuery
                .drop(1)
                .debounce(600)
                .distinctUntilChanged()
                .collectLatest {
                    refreshWithCurrentFilters()
                }
        }
    }

    private fun refreshWithCurrentFilters() {
        val state = _uiState.value
        val nextQuery = buildApiQuery(
            category = state.selectedCategory,
            searchQuery = state.searchQuery
        )

        if (nextQuery == selectedQuery && state.articles.isNotEmpty()) {
            return
        }

        selectedQuery = nextQuery
        currentPage = 1
        _uiState.value = NewsListUiState(
            selectedCategory = state.selectedCategory,
            searchQuery = state.searchQuery,
            authorSearchQuery = state.authorSearchQuery
        )
        loadNextPage()
    }

    private fun buildApiQuery(
        category: String,
        searchQuery: String
    ): String {
        val categoryTerm = categoryToQuery(category).takeIf { category.isNotBlank() }
        val terms = listOfNotNull(
            categoryTerm,
            searchQuery.trim().takeIf { it.isNotBlank() }
        )

        return if (terms.isEmpty()) DEFAULT_NEWS_QUERY else terms.joinToString(" ")
    }

    private fun applyAuthorFilterLocally(
        articles: List<NewsArticle>,
        authorSearchQuery: String
    ): List<NewsArticle> {
        if (authorSearchQuery.isBlank()) {
            return articles
        }

        return articles.filter { article ->
            article.author.contains(authorSearchQuery, ignoreCase = true)
        }
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
