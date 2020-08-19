package com.github.krupt.test.model

import org.springframework.data.domain.Sort

data class TestPage(
    val page: Int,
    val size: Int,
    val sort: List<TestSort>
)

data class TestSort(
    val property: String,
    val direction: Sort.Direction
)
