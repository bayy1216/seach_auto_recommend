package com.reditus.search.article

import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
) {
    @Transactional(readOnly = true)
    fun getArticlePaging(query: String?, pageable:Pageable): List<ArticleModel.Meta> {
        val page = if(query !=null){
            if(query.length in 2..40){
                applicationEventPublisher.publishEvent(ArticleEvent.Search(query))
            }
            articleRepository.findAllByTitleContaining(query, pageable)
        }else{
            articleRepository.findAll(pageable)
        }
        return page.content.map { ArticleModel.Meta.from(it) }
    }

    @Transactional(readOnly = true)
    fun getArticle(id: Long): ArticleModel.Meta {
        val article = articleRepository.findByIdOrNull(id) ?: throw NoSuchElementException("Article not found")
        return ArticleModel.Meta.from(article)
    }

    @Transactional
    fun createArticle(command: ArticleCommand.Create): ArticleModel.Meta {
        val article = Article.create(command)
        articleRepository.save(article)
        return ArticleModel.Meta.from(article)
    }
}
