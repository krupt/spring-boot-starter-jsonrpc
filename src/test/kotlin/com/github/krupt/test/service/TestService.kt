package com.github.krupt.test.service

import com.github.krupt.jsonrpc.annotation.JsonRpcService
import com.github.krupt.test.dto.TestRequest
import com.github.krupt.test.dto.TestResponse
import com.github.krupt.test.exception.TestException
import com.github.krupt.test.model.TestState
import com.github.krupt.test.model.TestUser
import java.util.UUID

@JsonRpcService
class TestService(
        private val testRunnable: Runnable
) {

    fun get(userId: UUID) = TestUser(userId)

    fun process(request: TestRequest): TestResponse {
        return TestResponse(1567)
    }

    fun processAsync(request: TestRequest) {
        testRunnable.run()
    }

    /*
    TODO Allow methods without arguments

    fun notificate() {
    }
    */

    fun jsonRpcException(request: TestRequest) {
        throw TestException(TestState("krupt"))
    }

    fun exception(request: TestRequest) {
        throw IllegalStateException("Invalid service state")
    }
}
