package com.reditus.search.article

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ArticleController(
    private val articleRepository: ArticleRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {

    @GetMapping("/api/articles")
    @Transactional(readOnly = true)
    fun getArticlePaging(
        @PageableDefault(page = 0, size = 10) pageable: Pageable,
        query: String?
    ): List<ArticleRes.ArticleMeta> {
        val page = if(query !=null){
            applicationEventPublisher.publishEvent(ArticleEvent.Search(query))

            articleRepository.findAllByTitleContaining(query, pageable)
        }else{
            articleRepository.findAll(pageable)
        }
        return page.content.map { ArticleRes.ArticleMeta.from(it) }
    }

    @GetMapping("/api/articles/{id}")
    @Transactional(readOnly = true)
    fun getArticle(
        @PathVariable("id") id: Long
    ): ArticleRes.ArticleMeta {
        val article = articleRepository.findById(id).get()
        return ArticleRes.ArticleMeta.from(article)
    }

    @PostMapping("/api/articles")
    @Transactional
    fun createArticle(
        req: ArticleReq.CreateArticle
    ): ArticleRes.ArticleMeta {
        val command = req.toCommand()
        val article = Article.create(command)
        articleRepository.save(article)
        return ArticleRes.ArticleMeta.from(article)
    }
}

class ArticleEvent{
    data class Search(
        val query: String
    )
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

class ArticleRes{
    data class ArticleMeta(
        val id: Long,
        val title: String,
    ){
        companion object{
            fun from(article: Article): ArticleMeta{
                return ArticleMeta(
                    id = article.id,
                    title = article.title
                )
            }
        }
    }
}