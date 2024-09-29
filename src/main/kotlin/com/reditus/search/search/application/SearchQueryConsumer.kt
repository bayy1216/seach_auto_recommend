package com.reditus.search.search.application

import com.reditus.search.article.ArticleEvent
import com.reditus.search.search.domain.SearchCount
import com.reditus.search.search.domain.SearchRecommend
import com.reditus.search.search.infrastructure.SearchRepository
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.time.LocalDateTime


@Component
class SearchQueryConsumer(
    private val mongoTemplate: MongoTemplate,
    private val searchRepository: SearchRepository,
) {

    @EventListener
    @Async
    fun consume(event: ArticleEvent.Search) {
        logger.info("SearchQueryConsumer consume: $event")

        val bulkOps: BulkOperations =
            mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SearchRecommend::class.java)

        // 1. 존재하면 count +1
        val existing = searchRepository.findByQuery(event.query)
        if (existing != null) {
            logger.info("Updating count for existing query: ${event.query}")
            bulkOps.updateOne(
                Query(Criteria.where("query").`is`(event.query)),
                Update().inc("count", 1).currentDate("updatedAt")
            )
        } else {
            // 2. 없으면 새로 생성. prefix 검색어도 count 0으로 생성
            createNewSearchRecommend(event, bulkOps)
        }


        bulkOps.execute()
    }

    private fun createNewSearchRecommend(event: ArticleEvent.Search, bulkOps: BulkOperations) {
        val searchRecommend = SearchRecommend.of(event.query)
        val prefixRecommends = event.generatePrefixQueries()

        // 기존 쿼리 확인
        val existingQueries = searchRepository.findAllByQueryIn(prefixRecommends).map { it.query }.toSet()

        // prefix 검색어 생성
        prefixRecommends.forEach { prefix ->
            if (!existingQueries.contains(prefix)) {
                logger.info("Inserting new prefix query: $prefix")
                bulkOps.insert(SearchRecommend.of(prefix, 0))
            }
        }

        // 주 검색어 추가
        logger.info("Inserting new search recommend: ${searchRecommend.query}")
        bulkOps.insert(searchRecommend)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SearchQueryConsumer::class.java)
    }
}