package com.reditus.search.search.application

import com.reditus.search.search.domain.SearchCount
import com.reditus.search.search.domain.SearchRecommend
import com.reditus.search.search.infrastructure.SearchRepository
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.query.*
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
            searchRepository.findAllByUpdatedAtAfterAndCountGreaterThan(
                updatedAt = LocalDateTime.now().minusDays(1),
                count = 0,
            )


        searchRecommends.forEach { newSearch ->
            if (newSearch.query.length < 2) return@forEach

            // 2. 해당 검석어의 recommend의 count top5보다 count가 높은 상위 검색어에만 저장
            val topSearches = getTopSearches(newSearch.getSearchCount())
            topSearches.forEach { topSearch ->
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

        // 결과를 List<SearchCount>로 변환
        return mongoTemplate.find<SearchRecommend>(
            Query(criteria)
        )
    }
}

/**
 * 해당 검색어의 prefix 검색어를 OR로 결합한 Criteria 반환
 */
private fun String.toPrefixRecommendOrCriteria(): Criteria {
    // abcd-> a, ab, abc | title equals 쿼리 criteria 생성
    val titleCriteria = (1 until this.length)
        .map { this.substring(0, it) }
        .map { SearchRecommend::query isEqualTo it }
    return Criteria().orOperator(titleCriteria) // OR로 결합
}