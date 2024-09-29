package com.reditus.search.search.application

import com.reditus.search.search.domain.SearchCount
import com.reditus.search.search.domain.SearchRecommend
import com.reditus.search.search.infrastructure.SearchRepository
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component

@Component
class SearchBatchUseCase(
    private val searchRepository: SearchRepository,
    private val mongoTemplate: MongoTemplate,
) {
    operator fun invoke() {
        //  abcd라는 검색어가 존재할시, 상위 ab혹은 abc의 캐시에 해당 검색어를 저장해야함


        // 1. 최근에 count가 업데이트된 검색어를 가져옴

        // 2. 해당 검석어의 상위 검색어의 캐시에 top5보다 count가 높은 상위 검색어에만 저장

    }

    private fun getTopSearches(query: String): List<SearchRecommend> {
        // MongoDB에서 상위 검색어를 가져오는 쿼리
        val criteria = Criteria.where("query").regex("^$query") // 검색어와 일치하거나 시작하는 검색어
        val query = Query(criteria).limit(5)
            .with(Sort.by(Sort.Direction.DESC, "count")) // 상위 5개 가져오기

        // 결과를 List<SearchCount>로 변환
        return mongoTemplate.find(query, SearchRecommend::class.java)
    }
}