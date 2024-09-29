package com.reditus.search.search

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed


@Document(collection = "search_recommend")
class SearchRecommend(
    @Id
    var id: ObjectId? = null,

    //unique
    @Indexed(unique = true)
    val query: String,
    var count: Int,
    val recommend: List<SearchCount> = emptyList(),
) {

    val searchCount: SearchCount
        get() = SearchCount(query, count)

    companion object{
        fun of(query: String): SearchRecommend{
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