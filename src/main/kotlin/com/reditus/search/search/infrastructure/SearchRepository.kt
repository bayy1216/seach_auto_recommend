package com.reditus.search.search.infrastructure

import com.reditus.search.search.domain.SearchRecommend
import org.springframework.data.mongodb.repository.MongoRepository

interface SearchRepository : MongoRepository<SearchRecommend, String> {
    fun findByQuery(query: String): SearchRecommend?
}