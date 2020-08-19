package com.github.krupt.test

import com.github.krupt.jsonrpc.config.JsonRpcConfigurationProperties
import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.dto.JsonRpcResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import org.assertj.core.error.ShouldMatchPattern.shouldMatch
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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

        assertAll(
            { response.id shouldBe 1 },
//                { response.id shouldBe "234" },
            { response.result shouldBe null },
            { response.error shouldNotBe null },
            { response.error!!.code shouldBe JsonRpcError.INVALID_REQUEST },
            { response.error!!.message shouldBe "Invalid request" },
            {
                val errorMessage = response.error!!.data as String
                shouldMatch(
                    errorMessage,
                    """.* value failed for JSON property jsonrpc due to missing \(therefore NULL\) .*"""
                )
            }
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

        assertAll(
            { response.id shouldBe "123" },
            { response.result shouldBe null },
            { response.error shouldNotBe null },
            { response.error!!.code shouldBe JsonRpcError.INVALID_REQUEST },
            { response.error!!.message shouldBe "Invalid request" },
            {
                @Suppress("UNCHECKED_CAST")
                val validationErrors = (response.error!!.data as List<String>)
                    .map {
                        it.substringAfter("on field '").substringBefore('\'') to it
                    }.toMap()
                validationErrors.size shouldBe 2
                assertAll(
                    {
                        shouldMatch(
                            validationErrors["jsonRpc"]!!,
                            """Field error in object 'jsonRpcRequest' on field 'jsonRpc': 
|rejected value \[2.1]; codes \[Pattern.*""".trimMargin()
                        )
                    },
                    {
                        shouldMatch(
                            validationErrors["method"]!!,
                            """Field error in object 'jsonRpcRequest' on field 'method': 
|rejected value \[ {3}]; codes \[NotBlank.*""".trimMargin()
                        )
                    }
                )
            }
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

        assertAll(
            { response.id shouldBe "456" },
            { response.result shouldBe null },
            { response.error shouldNotBe null },
            { response.error!!.code shouldBe JsonRpcError.INVALID_PARAMS },
            { response.error!!.message shouldBe "Invalid method parameter(s)" },
            {
                val errorMessage = response.error!!.data as String
                assertTrue(
                    Regex(
                        """.* value failed for JSON property name due to missing \(therefore NULL\) .*""",
                        RegexOption.DOT_MATCHES_ALL
                    ).matches(errorMessage),
                    errorMessage
                )
            }
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

        assertAll(
            { response.id shouldBe 567 },
            { response.result shouldBe null },
            { response.error shouldNotBe null },
            { response.error!!.code shouldBe JsonRpcError.INVALID_PARAMS },
            { response.error!!.message shouldBe "Request didn't pass validation" },
            {
                val errorMessages = response.error!!.data as List<String>
                errorMessages.size shouldBe 1
                val errorMessage = errorMessages[0]
                assertTrue(
                    errorMessage.startsWith(
                        "Field error in object 'TestRequest' on field 'name': rejected value [   ]; codes [NotBlank"
                    ),
                    errorMessage
                )
            }
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
