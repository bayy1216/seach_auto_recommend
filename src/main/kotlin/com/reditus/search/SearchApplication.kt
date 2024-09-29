package com.reditus.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@SpringBootApplication
@EnableAsync
class SearchApplication

fun main(args: Array<String>) {
    runApplication<SearchApplication>(*args)
}
