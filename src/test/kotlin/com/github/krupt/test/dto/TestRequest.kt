package com.github.krupt.test.dto

import javax.validation.Valid
import javax.validation.constraints.NotBlank

data class TestRequest(
    @field:NotBlank
    val name: String
)

data class ArrayTestRequest(
    @field:Valid
    val values: List<TestRequest>
)
