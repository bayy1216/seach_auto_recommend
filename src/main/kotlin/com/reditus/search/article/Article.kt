package com.reditus.search.article

import jakarta.persistence.*

@Entity
class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var title: String,
    var content: String,
    var updatedAt: Long = System.currentTimeMillis()
) {
    companion object{
        fun create(command: ArticleCommand.Create): Article{
            return Article(
                title = command.title,
                content = command.content
            )
        }
    }
}

class ArticleCommand{
    class Create(
        val title: String,
        val content: String
    )
}