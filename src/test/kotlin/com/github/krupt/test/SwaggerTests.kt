package com.github.krupt.test

import com.github.krupt.jsonrpc.config.JsonRpcProperties
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.matchers.maps.shouldContain
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
                "tags" to listOf("[JSON-RPC] testService"),
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
                "tags" to listOf("[JSON-RPC] testService"),
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

    @Test
    fun `Api documentation for simple method with simple parameter`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs["paths"]!! as Map<String, Any?>)["/${jsonRpcProperties.path}/json-rpc/testService.get"]!! as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
                "tags" to listOf("[JSON-RPC] testService"),
                "summary" to "get",
                "operationId" to "getUsingPOST",
                "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "parameters" to listOf(mapOf(
                        "in" to "body",
                        "name" to "userId",
                        "description" to "userId",
                        "required" to true,
                        "schema" to mapOf(
                                "type" to "string",
                                "format" to "uuid"
                        )
                )),
                "responses" to mapOf(
                        "200" to mapOf(
                                "description" to "OK",
                                "schema" to mapOf("\$ref" to "#/definitions/TestUser")
                        )
                ),
                "deprecated" to false
        )
    }

    @Test
    fun `Api documentation for simple method with array output`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs["paths"]!! as Map<String, Any?>)["/${jsonRpcProperties.path}/json-rpc/testService.list"]!! as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
                "tags" to listOf("[JSON-RPC] testService"),
                "summary" to "list",
                "operationId" to "listUsingPOST",
                "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "parameters" to listOf(mapOf(
                        "in" to "body",
                        "name" to "count",
                        "description" to "count",
                        "required" to true,
                        "schema" to mapOf(
                                "type" to "integer",
                                "format" to "int32"
                        )
                )),
                "responses" to mapOf(
                        "200" to mapOf(
                                "description" to "OK",
                                "schema" to mapOf(
                                        "type" to "array",
                                        "items" to mapOf(
                                                "\$ref" to "#/definitions/TestUser"
                                        )
                                )
                        )
                ),
                "deprecated" to false
        )
    }

    @Test
    fun `Api documentation for method without parameters`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs["paths"]!! as Map<String, Any?>)["/${jsonRpcProperties.path}/json-rpc/testService.call"]!! as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
                "tags" to listOf("[JSON-RPC] testService"),
                "summary" to "call",
                "operationId" to "callUsingPOST",
                "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "responses" to mapOf(
                        "200" to mapOf(
                                "description" to "OK"
                        )
                ),
                "deprecated" to false
        )
    }

    @Test
    fun `Api documentation for main method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs["paths"]!! as Map<String, Any?>)["/${jsonRpcProperties.path}"]!! as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
                "tags" to listOf("json-rpc-controller"),
                "summary" to "The endpoint that handles all JSON-RPC requests",
                "description" to """Read more about <a href="https://www.jsonrpc.org/specification">JSON-RPC 2.0 Specification</a>""",
                "operationId" to "handleUsingPOST",
                "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
                "produces" to listOf(MediaType.ALL_VALUE),
                "parameters" to listOf(mapOf(
                        "in" to "body",
                        "name" to "request",
                        "description" to "request",
                        "required" to true,
                        "schema" to mapOf("\$ref" to "#/definitions/JsonRpcRequest«object»")
                )),
                "responses" to mapOf(
                        "200" to mapOf(
                                "description" to "OK",
                                "schema" to mapOf("\$ref" to "#/definitions/JsonRpcResponse«object»")
                        )
                ),
                "deprecated" to false
        )
    }

    @Test
    fun `Api documentation default info`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        (apiDocs["info"]!! as Map<String, Any?>)
                .shouldContain("title", "Api Documentation")
    }
}
