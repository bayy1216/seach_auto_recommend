package com.reditus.search.search

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    private val searchRepository: SearchRepository
) {
    @GetMapping("/api/search")
    fun search(
        query: String
    ): List<String> {
        return searchRepository.findByQuery(query)?.toRecommend()
            ?: emptyList()
    }
}

fun SearchRecommend.toRecommend(): List<String> {
    // 본인 + 하부 추천어
    val recommendations = recommend + searchCount

    return recommendations.sortedByDescending { it.count }.map { it.query }
}