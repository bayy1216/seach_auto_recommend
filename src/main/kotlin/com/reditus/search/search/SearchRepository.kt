package com.reditus.search.search

import org.springframework.data.mongodb.repository.MongoRepository

interface SearchRepository : MongoRepository<SearchRecommend, String> {
    fun findByQuery(query: String): SearchRecommend?
}