package com.reditus.search.article

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
class ArticleController(
    private val articleService: ArticleService,
) {

    @GetMapping("/api/articles")
    @Transactional(readOnly = true)
    fun getArticlePaging(
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
        query: String?
    ): List<ArticleModel.Meta> {
        return articleService.getArticlePaging(query, pageable)
    }

    @GetMapping("/api/articles/{id}")
    @Transactional(readOnly = true)
    fun getArticle(
        @PathVariable("id") id: Long
    ): ArticleModel.Meta {
        return articleService.getArticle(id)
    }

    @PostMapping("/api/articles")
    @Transactional
    fun createArticle(
        @RequestBody req: ArticleReq.CreateArticle
    ): ArticleModel.Meta {
        val command = req.toCommand()
        return articleService.createArticle(command)
    }
}


class ArticleReq{
    data class CreateArticle(
        val title: String,
        val content: String
    ){
        fun toCommand(): ArticleCommand.Create{
            return ArticleCommand.Create(
                title = title,
                content = content
            )
        }
    }
}

