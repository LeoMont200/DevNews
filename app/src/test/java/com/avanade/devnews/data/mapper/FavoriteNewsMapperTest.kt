package com.avanade.devnews.data.mapper

import com.avanade.devnews.sampleArticle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FavoriteNewsMapperTest {

    @Test
    fun `maps article to favorite entity`() {
        val article = sampleArticle()

        val result = article.toFavoriteEntity(userId = "user-1")

        assertEquals("user-1", result.userId)
        assertEquals(article.articleUrl, result.articleUrl)
        assertEquals(article.title, result.title)
        assertEquals(article.imageUrl, result.imageUrl)
    }

    @Test
    fun `maps favorite entity back to domain`() {
        val entity = sampleArticle(imageUrl = null).toFavoriteEntity(userId = "user-1")

        val result = entity.toDomain()

        assertEquals(entity.articleUrl, result.articleUrl)
        assertEquals(entity.description, result.description)
        assertNull(result.imageUrl)
    }
}
