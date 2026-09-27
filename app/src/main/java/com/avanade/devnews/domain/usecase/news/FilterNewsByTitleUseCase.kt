package com.avanade.devnews.domain.usecase.news

import com.avanade.devnews.domain.model.NewsArticle
import java.text.Normalizer
import java.util.Locale
import javax.inject.Inject

class FilterNewsByTitleUseCase @Inject constructor() {

    operator fun invoke(articles: List<NewsArticle>, query: String): List<NewsArticle> {
        val terms = query
            .trim()
            .split(Regex("\\s+"))
            .map { it.normalizeForSearch() }
            .filter { it.isNotBlank() }

        if (terms.isEmpty()) {
            return articles
        }

        return articles.filter { article ->
            val normalizedTitle = article.title.normalizeForSearch()
            terms.all { term -> normalizedTitle.contains(term) }
        }
    }

    private fun String.normalizeForSearch(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return normalized
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
            .lowercase(Locale.ROOT)
    }
}
