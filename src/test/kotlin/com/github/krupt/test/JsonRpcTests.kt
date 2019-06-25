package com.github.krupt.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import com.github.krupt.jsonrpc.config.JsonRpcProperties
import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.dto.JsonRpcRequest
import com.github.krupt.jsonrpc.dto.JsonRpcResponse
import com.github.krupt.test.dto.TestRequest
import com.github.krupt.test.dto.TestResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForObject
import org.springframework.boot.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class JsonRpcTests {

    @MockkBean(relaxed = true)
    private lateinit var testRunnable: Runnable

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jsonRpcProperties: JsonRpcProperties

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `application calls simple method and returns result`() {
        call<TestResponse>(
                JsonRpcRequest(
                        "12345",
                        "testService.process",
                        TestRequest("krupt"),
                        "2.0"
                )
        ) shouldBe JsonRpcResponse(
                "12345",
                TestResponse(1567)
        )
    }

    @Test
    fun `application calls async method and returns empty result with identifier`() {
        call<Any>(
                JsonRpcRequest(
                        "9876",
                        "testService.processAsync",
                        TestRequest("krupt"),
                        "2.0"
                )
        ) shouldBe JsonRpcResponse(
                "9876",
                null
        )
    }

    @Test
    fun `application calls simple method without id and returns empty response`() {
        call<Any>(
                JsonRpcRequest(
                        method = "testService.process",
                        params = TestRequest("krupt"),
                        jsonRpc = "2.0"
                )
        ) shouldBe null
    }

    @Test
    fun `application calls async method without id and returns empty response`() {
        call<Any>(
                JsonRpcRequest(
                        method = "testService.process",
                        params = TestRequest("krupt"),
                        jsonRpc = "2.0"
                )
        ) shouldBe null
    }

    @Test
    fun `application calls method that throwing JSON-RPC exception`() {
        call<Any>(
                JsonRpcRequest(
                        1234567,
                        "testService.jsonRpcException",
                        TestRequest("krupt"),
                        "2.0"
                )
        ) shouldBe JsonRpcResponse(
                1234567,
                error = JsonRpcError(
                        -29345,
                        "Test state is incorrect",
                        mapOf("userId" to "krupt")
                )
        )
    }

    @Test
    fun `application calls method that throwing exception`() {
        call<Any>(
                JsonRpcRequest(
                        mapOf("id" to 6709),
                        "testService.exception",
                        TestRequest("krupt"),
                        "2.0"
                )
        ) shouldBe JsonRpcResponse(
                mapOf("id" to 6709),
                error = JsonRpcError(
                        JsonRpcError.INTERNAL_ERROR,
                        "Unhandled exception",
                        "java.lang.IllegalStateException: Invalid service state"
                )
        )
    }

    private inline fun <reified R> call(request: JsonRpcRequest<Any>): JsonRpcResponse<R>? {
        val rawResponse: JsonRpcResponse<Map<String, Any?>>? = restTemplate.postForObject(
                "http://localhost:$port/${jsonRpcProperties.path}",
                request
        )

        return rawResponse?.let {
            val result: R? = rawResponse.result?.let {
                objectMapper.convertValue(it)
            }

            return JsonRpcResponse(
                    rawResponse.id,
                    result,
                    rawResponse.error,
                    rawResponse.jsonRpc
            )
        }
    }
}