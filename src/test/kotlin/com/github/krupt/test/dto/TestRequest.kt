package com.github.krupt.test.dto

import javax.validation.constraints.NotBlank

data class TestRequest(
        @field:NotBlank
        val name: String
)
