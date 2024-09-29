package com.reditus.search.article

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long> {
    fun findAllByTitleContaining(title: String, pageable: Pageable): Page<Article>
}