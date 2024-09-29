package com.reditus.search.search.infrastructure

import com.reditus.search.search.domain.SearchRecommend
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import java.time.LocalDateTime

interface SearchRepository : MongoRepository<SearchRecommend, String> {
    fun findByQuery(query: String): SearchRecommend?
    fun findAllByQueryIn(queries: List<String>): List<SearchRecommend>
    fun findAllByUpdatedAtAfterAndCountGreaterThan(
        updatedAt: LocalDateTime,
        count: Int
    ): List<SearchRecommend>

    @Query("{ 'query' : ?0}")
    @Update("{ '\$inc' : { 'count' : ?1 } }") // kotlin이라 \$를 사용해야함
    fun addCountByQuery(query: String, count: Int)
}