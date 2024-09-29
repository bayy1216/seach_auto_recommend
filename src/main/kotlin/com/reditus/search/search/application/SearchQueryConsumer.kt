package com.reditus.search.search.application

import com.reditus.search.article.ArticleEvent
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


@Component
class SearchQueryConsumer(
    private val mongoTemplate: MongoTemplate,
    private val searchRepository: SearchRepository
) {

    @EventListener
    @Async
    fun consume(event: ArticleEvent.Search) {
        logger.info("SearchQueryConsumer consume: $event")

        val bulkOps: BulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SearchRecommend::class.java)

        // 1. 존재하면 count +1
        searchRepository.findByQuery(event.query)?.let {
            bulkOps.updateOne(
                Query(Criteria.where("query").`is`(event.query)),
                Update().inc("count", 1)
            ).execute()
            return
        }

        // 2. 없으면 새로 생성

        val searchRecommend = SearchRecommend.of(event.query)
        searchRepository.save(searchRecommend)
    }

    companion object{
        private val logger = LoggerFactory.getLogger(SearchQueryConsumer::class.java)
    }
}