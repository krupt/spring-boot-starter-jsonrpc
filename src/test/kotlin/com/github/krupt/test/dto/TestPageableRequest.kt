package com.github.krupt.test.dto

import org.springframework.data.domain.Pageable

data class TestPageableRequest(
    val name: String,
    val pageable: Pageable
)
