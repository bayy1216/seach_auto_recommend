package com.reditus.search.search.application

import com.reditus.search.search.domain.SearchCount
import com.reditus.search.search.domain.SearchRecommend
import com.reditus.search.search.infrastructure.SearchRepository
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class SearchBatchUseCase(
    private val searchRepository: SearchRepository,
    private val mongoTemplate: MongoTemplate,
) {
    operator fun invoke() {
        //  abcd라는 검색어가 존재할시, 상위 ab혹은 abc의 캐시에 해당 검색어를 저장해야함


        // 1. 최근에 count가 업데이트된 검색어를 가져옴
        val searchRecommends: List<SearchRecommend> =
            searchRepository.findAllByUpdatedAtAfter(LocalDateTime.now().minusDays(1))


        searchRecommends.forEach {newSearch ->
            println("search: ${newSearch.count} ${newSearch.query}")
            // 2. 해당 검석어의 recommend의 count top5보다 count가 높은 상위 검색어에만 저장
            val topSearches = getTopSearches(newSearch.getSearchCount())
            topSearches.forEach{topSearch->
                println("top search: ${topSearch.query} ${topSearch.count}")
                topSearch.addRecommendAndCompaction(newSearch.getSearchCount())
            }
            searchRepository.saveAll(topSearches)
        }



    }

    private fun getTopSearches(searchCount: SearchCount): List<SearchRecommend> {
        // MongoDB에서 상위 검색어를 가져오는 쿼리
        // 검색어와 일치하지 않고, 해당 검색어로 시작하는 검색어
        val orTitleCriteria = searchCount.query.toPrefixRecommendOrCriteria()


        val criteria = Criteria().andOperator(
            orTitleCriteria, // OR 조건을 포함한 titleCriteria
            //TODO :searchCount.count가 recommend 리스트의 최소 count보다 큰 경우만 가져오기
        )

        val query = Query(criteria).limit(5)
            .with(Sort.by(Sort.Direction.DESC, "count")) // 상위 5개 가져오기, 해당 쿼리에서 count가 높은 5개 반환

        // 결과를 List<SearchCount>로 변환
        return mongoTemplate.find(query, SearchRecommend::class.java)
    }
}

/**
 * 해당 검색어의 prefix 검색어를 OR로 결합한 Criteria 반환
 */
private fun String.toPrefixRecommendOrCriteria(): Criteria {
    // abcd-> a, ab, abc
    val prefixQueries = mutableListOf<String>()
    for(i in 1 until this.length){
        prefixQueries.add(this.substring(0, i))
    }

    // query가 prefixQueries에 포함되는 경우
    val titleCriteria= prefixQueries.map {
        Criteria.where("query").isEqualTo(it)
    }
    return Criteria().orOperator(*titleCriteria.toTypedArray()) // OR로 결합
}