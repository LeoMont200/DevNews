package com.avanade.devnews.data.mapper

import com.avanade.devnews.data.remote.dto.NewsArticleDto
import com.avanade.devnews.data.remote.dto.NewsSourceDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NewsMapperTest {

    @Test
    fun `returns null when title is blank`() {
        val dto = NewsArticleDto(
            source = NewsSourceDto(id = null, name = "Fonte"),
            author = "Autor",
            title = " ",
            description = "Descricao",
            url = "https://example.com",
            urlToImage = null,
            publishedAt = "2026-10-06",
            content = "Conteudo"
        )

        assertNull(dto.toDomain())
    }

    @Test
    fun `returns null when url is blank`() {
        val dto = NewsArticleDto(
            source = NewsSourceDto(id = null, name = "Fonte"),
            author = "Autor",
            title = "Titulo",
            description = "Descricao",
            url = "",
            urlToImage = null,
            publishedAt = "2026-10-06",
            content = "Conteudo"
        )

        assertNull(dto.toDomain())
    }

    @Test
    fun `maps dto to domain and strips truncated suffix`() {
        val dto = NewsArticleDto(
            source = null,
            author = null,
            title = "Titulo",
            description = null,
            url = "https://example.com",
            urlToImage = "https://example.com/image.png",
            publishedAt = null,
            content = "Texto da noticia... [+1168 chars]"
        )

        val result = dto.toDomain()

        assertEquals("NewsAPI", result?.sourceName)
        assertEquals("", result?.author)
        assertEquals("", result?.description)
        assertEquals("Texto da noticia...", result?.content)
        assertEquals("https://example.com/image.png", result?.imageUrl)
        assertEquals("", result?.publishedAt)
    }
}
