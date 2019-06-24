package com.github.krupt.test

import com.github.krupt.jsonrpc.config.JsonRpcProperties
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.matchers.maps.shouldContainExactly
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.MediaType

@Suppress("UNCHECKED_CAST")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SwaggerTests {

    @MockkBean(relaxed = true)
    private lateinit var testRunnable: Runnable

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jsonRpcProperties: JsonRpcProperties

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `Api documentation for simple method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs["paths"]!! as Map<String, Any?>)["/${jsonRpcProperties.path}/json-rpc/testService.process"]!! as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
                "tags" to listOf("testService"),
                "summary" to "process",
                "operationId" to "processUsingPOST",
                "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "parameters" to listOf(mapOf(
                        "in" to "body",
                        "name" to "request",
                        "description" to "request",
                        "required" to true,
                        "schema" to mapOf("\$ref" to "#/definitions/TestRequest")
                )),
                "responses" to mapOf(
                        "200" to mapOf(
                                "description" to "OK",
                                "schema" to mapOf("\$ref" to "#/definitions/TestResponse")
                        )
                ),
                "deprecated" to false
        )
    }

    @Test
    fun `Api documentation for async method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs["paths"]!! as Map<String, Any?>)["/${jsonRpcProperties.path}/json-rpc/testService.processAsync"]!! as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
                "tags" to listOf("testService"),
                "summary" to "processAsync",
                "operationId" to "processAsyncUsingPOST",
                "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "parameters" to listOf(mapOf(
                        "in" to "body",
                        "name" to "request",
                        "description" to "request",
                        "required" to true,
                        "schema" to mapOf("\$ref" to "#/definitions/TestRequest")
                )),
                "responses" to mapOf(
                        "200" to mapOf(
                                "description" to "OK"
                        )
                ),
                "deprecated" to false
        )
    }
}
