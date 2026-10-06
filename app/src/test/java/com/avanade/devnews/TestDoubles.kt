package com.avanade.devnews

import com.avanade.devnews.data.local.dao.FavoriteNewsDao
import com.avanade.devnews.data.local.entity.FavoriteNewsEntity
import com.avanade.devnews.domain.model.NewsArticle
import com.avanade.devnews.domain.model.NewsPage
import com.avanade.devnews.domain.model.User
import com.avanade.devnews.domain.repository.AuthRepository
import com.avanade.devnews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

data class NewsRequest(
    val page: Int,
    val pageSize: Int,
    val query: String
)

class FakeNewsRepository(
    private val responses: MutableList<Result<NewsPage>> = mutableListOf()
) : NewsRepository {
    val requests = mutableListOf<NewsRequest>()

    override suspend fun getNews(page: Int, pageSize: Int, query: String): Result<NewsPage> {
        requests += NewsRequest(page = page, pageSize = pageSize, query = query)
        return responses.removeFirstOrNull() ?: Result.success(NewsPage(emptyList(), 0))
    }

    fun enqueue(result: Result<NewsPage>) {
        responses += result
    }
}

class FakeFavoriteNewsDao : FavoriteNewsDao {
    private val favoritesByUser = linkedMapOf<String, MutableList<FavoriteNewsEntity>>()
    private val favoritesFlowByUser = linkedMapOf<String, MutableStateFlow<List<FavoriteNewsEntity>>>()
    private val favoriteUrlsFlowByUser = linkedMapOf<String, MutableStateFlow<List<String>>>()

    override suspend fun upsertFavorite(news: FavoriteNewsEntity) {
        val items = favoritesByUser.getOrPut(news.userId) { mutableListOf() }
        items.removeAll { it.articleUrl == news.articleUrl }
        items += news
        publish(news.userId)
    }

    override suspend fun removeFavorite(userId: String, articleUrl: String) {
        favoritesByUser[userId]?.removeAll { it.articleUrl == articleUrl }
        publish(userId)
    }

    override fun observeFavorites(userId: String): Flow<List<FavoriteNewsEntity>> {
        return favoritesFlowByUser.getOrPut(userId) {
            MutableStateFlow(favoritesByUser[userId].orEmpty())
        }
    }

    override fun observeFavoriteUrls(userId: String): Flow<List<String>> {
        return favoriteUrlsFlowByUser.getOrPut(userId) {
            MutableStateFlow(favoritesByUser[userId].orEmpty().map { it.articleUrl })
        }
    }

    fun seed(userId: String, favorites: List<FavoriteNewsEntity>) {
        favoritesByUser[userId] = favorites.toMutableList()
        publish(userId)
    }

    private fun publish(userId: String) {
        val favorites = favoritesByUser[userId].orEmpty().toList()
        favoritesFlowByUser.getOrPut(userId) { MutableStateFlow(emptyList()) }.value = favorites
        favoriteUrlsFlowByUser.getOrPut(userId) { MutableStateFlow(emptyList()) }.value =
            favorites.map { it.articleUrl }
    }
}

class FakeAuthRepository(
    private var currentUser: User? = null
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<User> = error("Not used")
    override fun getCurrentUser(): User? = currentUser
    override fun setRememberMe(enabled: Boolean) = Unit
    override fun isRememberMeEnabled(): Boolean = false
    override fun hasActiveSession(): Boolean = currentUser != null
    override fun logout() = Unit
    override suspend fun register(email: String, password: String): Result<User> = error("Not used")
    override suspend fun recoverPassword(email: String): Result<Unit> = error("Not used")

    fun setCurrentUser(user: User?) {
        currentUser = user
    }
}

fun sampleArticle(
    articleUrl: String = "https://example.com/article-1",
    author: String = "Autor Teste",
    title: String = "Titulo Teste",
    description: String = "Descricao Teste",
    content: String = "Conteudo Teste",
    sourceName: String = "Fonte",
    imageUrl: String? = "https://example.com/image.png",
    publishedAt: String = "2026-10-06T10:00:00Z"
): NewsArticle {
    return NewsArticle(
        sourceName = sourceName,
        author = author,
        title = title,
        description = description,
        content = content,
        articleUrl = articleUrl,
        imageUrl = imageUrl,
        publishedAt = publishedAt
    )
}
