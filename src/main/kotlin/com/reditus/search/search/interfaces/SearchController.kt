package com.reditus.search.search.interfaces

import com.reditus.search.search.application.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SearchController(
    private val searchService: SearchService,
) {
    @GetMapping("/api/search")
    fun search(
        query: String
    ): List<String> {
        return searchService.getSearchCommends(query)
    }
}
