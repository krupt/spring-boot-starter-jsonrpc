package com.github.krupt.test.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class ReThrowingException(override val message: String?) : RuntimeException()
