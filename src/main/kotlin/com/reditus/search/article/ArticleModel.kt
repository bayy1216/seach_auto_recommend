package com.reditus.search.article


class ArticleModel{
    data class Meta(
        val id: Long,
        val title: String,
    ){
        companion object{
            fun from(article: Article): Meta{
                return Meta(
                    id = article.id,
                    title = article.title
                )
            }
        }
    }
}

class ArticleEvent{
    data class Search(
        val query: String
    ){
        fun generatePrefixQueries(): List<String>{
            return (1 until query.length).map { index ->
                query.substring(0, index)
            }
        }
    }
}
