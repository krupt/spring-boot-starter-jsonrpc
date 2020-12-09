package com.github.krupt.test

import com.github.krupt.jsonrpc.config.JsonRpcConfigurationProperties
import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.dto.JsonRpcResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.exchange
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.RequestEntity
import java.net.URI

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class JsonRpcValidationTests {

    @MockkBean(relaxed = true)
    private lateinit var testRunnable: Runnable

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jsonRpcConfigurationProperties: JsonRpcConfigurationProperties

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `successful response doesn't have error field`() {
        val request = """
                    {
                        "method": "testService.call",
                        "id": "345",
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        val response = restTemplate.exchange<Map<String, Any>>(RequestEntity(
            request,
            headers,
            HttpMethod.POST,
            URI.create("http://localhost:$port/${jsonRpcConfigurationProperties.path}")
        )).body!!

        response shouldContainExactly mapOf(
            "id" to "345",
            "result" to null,
            "jsonrpc" to "2.0"
        )
    }

    @Test
    fun `request with invalid JSON fails`() {
        call(
            "{[}"
        ) shouldBe JsonRpcResponse(
            error = JsonRpcError(JsonRpcError.PARSE_ERROR, "Parse error")
        )
    }

    @Test
    fun `request with missing JSON-RPC version fails`() {
        val response = call(
            """
                    {
                        "method": "testService.process",
                        "id": "234"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            id = 1, // "234",
            error = JsonRpcError(
                JsonRpcError.INVALID_REQUEST,
                "Invalid request",
                "jsonrpc must not be null"
            )
        )
    }

    @Test
    fun `request with invalid JSON-RPC version fails`() {
        val response = call(
            """
                    {
                        "method": "   ",
                        "id": "123",
                        "jsonrpc": "2.1"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            "123",
            error = JsonRpcError(
                JsonRpcError.INVALID_REQUEST,
                "Invalid request",
                listOf(
                    "jsonRpc must be exactly 2.0",
                    "method must not be blank"
                )
            )
        )
    }

    @Test
    fun `request with missing params fails`() {
        call(
            """
                    {
                        "method": "testService.process",
                        "params": null,
                        "id": "345",
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        ) shouldBe JsonRpcResponse(
            id = "345",
            error = JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Invalid method parameter(s)",
                "Params can't be null"
            )
        )
    }

    @Test
    fun `request with invalid params with missing required field fails`() {
        val response = call(
            """
                    {
                        "method": "testService.process",
                        "params": {
                            "hello": ""
                        },
                        "id": "456",
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            id = "456",
            error = JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Invalid method parameter(s)",
                "params.name must not be null"
            )
        )
    }

    @Test
    fun `request with invalid params with missing required field in nested array fails`() {
        val response = call(
            """
                    {
                        "method": "testService.processArray",
                        "params": {
                            "values": [
                                {
                                    "name": "value1"
                                },
                                {
                                    "hello": ""
                                }
                            ]
                        },
                        "id": "456",
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            id = "456",
            error = JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Invalid method parameter(s)",
                "params.values[1].name must not be null"
            )
        )
    }

    @Test
    fun `request with invalid params fails`() {
        val response = call(
            """
                    {
                        "method": "testService.process",
                        "params": [
                            {
                                "name": "value1"
                            }
                        ],
                        "id": "456",
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            id = "456",
            error = JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Invalid method parameter(s)",
                "Cannot deserialize instance of `com.github.krupt.test.dto.TestRequest` out of START_ARRAY token" +
                    "\n at [Source: UNKNOWN; line: -1, column: -1]"
            )
        )
    }

    @Test
    fun `request with invalid params with invalid field fails`() {
        val response = call(
            """
                    {
                        "method": "testService.process",
                        "params": {
                            "name": "   "
                        },
                        "id": 567,
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            567,
            error = JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Request didn't pass validation",
                listOf("params.name must not be blank")
            )
        )
    }

    @Test
    fun `request with invalid params with invalid array field fails`() {
        val response = call(
            """
                    {
                        "method": "testService.processArray",
                        "params": {
                            "values": [
                                {
                                    "name": "21"
                                },
                                {
                                    "name": "    "
                                }
                            ]
                        },
                        "id": 567,
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        )!!

        response shouldBe JsonRpcResponse(
            567,
            error = JsonRpcError(
                JsonRpcError.INVALID_PARAMS,
                "Request didn't pass validation",
                listOf("params.values[1].name must not be blank")
            )
        )
    }

    @Test
    fun `request for unknown method fails`() {
        call(
            """
                    {
                        "method": "testService.processAs",
                        "params": {
                            "name": "hello"
                        },
                        "id": "678",
                        "jsonrpc": "2.0"
                    }
                """.trimIndent()
        ) shouldBe JsonRpcResponse(
            id = "678",
            error = JsonRpcError(JsonRpcError.METHOD_NOT_FOUND, "Method not found")
        )
    }

    private fun call(request: String): JsonRpcResponse<Any>? {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON

        return restTemplate.exchange<JsonRpcResponse<Any>>(RequestEntity(
            request,
            headers,
            HttpMethod.POST,
            URI.create("http://localhost:$port/${jsonRpcConfigurationProperties.path}")
        )).body
    }
}
