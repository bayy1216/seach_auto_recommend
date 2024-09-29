package com.reditus.search.search.domain

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime


@Document(collection = "search_recommend")
class SearchRecommend(
    @Id
    var id: ObjectId? = null,

    //unique
    @Indexed(unique = true)
    val query: String,
    var count: Int,
    val recommend: MutableList<SearchCount> = mutableListOf(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
) {

    fun getSearchCount(): SearchCount{
        return SearchCount(query, count)
    }

    /**
     * 추천어 추가 및 정렬 수행
     * 이후 추천어 리스트가 size를 초과하면 마지막 요소 삭제
     */
    fun addRecommendAndCompaction(searchCount: SearchCount, size: Int = 5) {
        recommend.removeIf { it.query == searchCount.query }
        recommend.add(searchCount)
        recommend.sortByDescending { it.count }
        if(recommend.size > size) {
            recommend.removeAt(recommend.size - 1)
        }
    }

    companion object{
        fun of(query: String): SearchRecommend {
            return SearchRecommend(
                query = query,
                count = 1
            )
        }
    }
}

class SearchCount(
    val query: String,
    val count: Int
)