package com.reditus.search.search.application

import com.reditus.search.search.domain.SearchRecommend
import com.reditus.search.search.infrastructure.SearchRepository
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val searchRepository: SearchRepository
) {
    fun getSearchCommends(query: String): List<String> {
        val searchRecommend: SearchRecommend? = searchRepository.findByQuery(query)

        return searchRecommend?.toRecommend() ?: emptyList()
    }

    fun getAll(): List<SearchRecommend> {
        return searchRepository.findAll()
    }
}


fun SearchRecommend.toRecommend(): List<String> {
    // 본인 + 하부 추천어
    val recommendations = recommend + getSearchCount()

    return recommendations
        .filter { it.count > 0 }
        .sortedByDescending { it.count }.map { it.query }
}