package com.reditus.search.search.interfaces

import com.reditus.search.search.application.SearchBatchUseCase
import com.reditus.search.search.application.SearchService
import com.reditus.search.search.domain.SearchRecommend
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    private val searchService: SearchService,
    private val searchBatchUseCase: SearchBatchUseCase
) {
    @GetMapping("/api/search")
    fun search(
        query: String
    ): List<String> {
        return searchService.getSearchCommends(query)
    }


    @GetMapping("/api/searches")
    fun getAll(): List<SearchRecommend> {
        return searchService.getAll()
    }


    @PostMapping("/api/searches")
    fun batch() {
        searchBatchUseCase()
    }
}
