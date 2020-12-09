package com.github.krupt.test

import com.github.krupt.jsonrpc.config.JsonRpcConfigurationProperties
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.maps.shouldContain
import io.kotlintest.matchers.maps.shouldContainExactly
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
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
    private lateinit var jsonRpcConfigurationProperties: JsonRpcConfigurationProperties

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `api documentation for simple method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/testService.process") as Map<String, Any?>

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
    fun `api documentation for async method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/testService.processAsync") as Map<String, Any?>

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
    fun `api documentation for simple method with simple parameter`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/testService.get") as Map<String, Any?>

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
    fun `api documentation for simple method with array output`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/testService.list") as Map<String, Any?>

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
    fun `api documentation for method without parameters`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/testService.call") as Map<String, Any?>

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
    fun `api documentation for main method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}") as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("json-rpc-controller"),
            "summary" to "The endpoint that handles all JSON-RPC requests",
            "description" to
                """Read more about <a href="https://www.jsonrpc.org/specification">JSON-RPC 2.0 Specification</a>""",
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
    fun `api documentation default info`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        (apiDocs["info"]!! as Map<String, Any?>)
            .shouldContain("title", "Api Documentation")
    }

    @Test
    fun `api documentation for method with pageable`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/testService.pageable") as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("[JSON-RPC] testService"),
            "summary" to "pageable",
            "operationId" to "pageableUsingPOST",
            "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "parameters" to listOf(mapOf(
                "in" to "body",
                "name" to "pageable",
                "description" to "pageable",
                "required" to true,
                "schema" to mapOf("\$ref" to "#/definitions/Pageable")
            )),
            "responses" to mapOf(
                "200" to mapOf(
                    "description" to "OK",
                    "schema" to mapOf("\$ref" to "#/definitions/TestPage")
                )
            ),
            "deprecated" to false
        )
    }

    @Test
    fun `api documentation for pageable and models with pageable`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val definitions = (apiDocs["definitions"]!! as Map<String, Any?>)

        assertAll(
            {
                definitions["Pageable"] as Map<String, Any?> shouldContainExactly mapOf(
                    "title" to "Pageable",
                    "type" to "object",
                    "properties" to mapOf(
                        "page" to mapOf(
                            "type" to "integer",
                            "format" to "int32"
                        ),
                        "size" to mapOf(
                            "type" to "integer",
                            "format" to "int32"
                        ),
                        "sort" to mapOf(
                            "type" to "array",
                            "items" to mapOf(
                                "\$ref" to "#/definitions/Sort"
                            )
                        )
                    )
                )
            },
            {
                definitions["Sort"] as Map<String, Any?> shouldContainExactly mapOf(
                    "title" to "Sort",
                    "type" to "object",
                    "required" to listOf(
                        "property"
                    ),
                    "properties" to mapOf(
                        "property" to mapOf(
                            "type" to "string"
                        ),
                        "direction" to mapOf(
                            "type" to "string",
                            "enum" to listOf(
                                "ASC",
                                "DESC"
                            )
                        )
                    )
                )
            },
            {
                definitions["TestPageableRequest"] as Map<String, Any?> shouldContainExactly mapOf(
                    "title" to "TestPageableRequest",
                    "type" to "object",
                    "required" to listOf(
                        "name",
                        "pageable"
                    ),
                    "properties" to mapOf(
                        "name" to mapOf(
                            "type" to "string"
                        ),
                        "pageable" to mapOf(
                            "\$ref" to "#/definitions/Pageable"
                        )
                    )
                )
            }
        )
    }

    @Test
    fun `api documentation without CGLIB methods`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        (apiDocs["paths"]!! as Map<String, Any?>).keys shouldContainExactlyInAnyOrder listOf(
            "/testApi",
            "/testApi/json-rpc/customTestService.test",
            "/testApi/json-rpc/testService.call",
            "/testApi/json-rpc/testService.exception",
            "/testApi/json-rpc/testService.get",
            "/testApi/json-rpc/testService.jsonRpcException",
            "/testApi/json-rpc/testService.list",
            "/testApi/json-rpc/testService.pageable",
            "/testApi/json-rpc/testService.pageableWrapper",
            "/testApi/json-rpc/testService.process",
            "/testApi/json-rpc/testService.processArray",
            "/testApi/json-rpc/testService.processAsync",
            "/testApi/json-rpc/testService.reThrowingException",
            "/testApi/json-rpc/method.test",
            "/testApi/json-rpc/method.testMethodWithoutInput",
            "/testApi/json-rpc/method.testMethodWithoutResult",
            "/testApi/json-rpc/method.testMethodWithException",
            "/testApi/json-rpc/method.testJavaMethodWithoutInput",
            "/testApi/json-rpc/method.testJavaMethodWithoutResult"
        )

        (apiDocs["tags"]!! as List<Map<String, String>>)
            .map {
                it["name"]
            } shouldContainExactlyInAnyOrder listOf(
            "[JSON-RPC] customTestService",
            "[JSON-RPC] method",
            "[JSON-RPC] testService",
            "json-rpc-controller"
        )
    }

    @Test
    fun `api documentation for simple json rpc method`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/method.test") as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("[JSON-RPC] method"),
            "summary" to "test",
            "operationId" to "testUsingPOST_1",
            "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "parameters" to listOf(mapOf(
                "in" to "body",
                "name" to "input",
                "description" to "input",
                "required" to true,
                "schema" to mapOf(
                    "type" to "string",
                    "format" to "uuid"
                )
            )),
            "responses" to mapOf(
                "200" to mapOf(
                    "description" to "OK",
                    "schema" to mapOf("\$ref" to "#/definitions/TestState")
                )
            ),
            "deprecated" to false
        )
    }

    @Test
    fun `api documentation for json rpc method without parameters`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/method.testMethodWithoutInput")
            as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("[JSON-RPC] method"),
            "summary" to "testMethodWithoutInput",
            "operationId" to "testMethodWithoutInputUsingPOST",
            "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "responses" to mapOf(
                "200" to mapOf(
                    "description" to "OK",
                    "schema" to mapOf("\$ref" to "#/definitions/TestState")
                )
            ),
            "deprecated" to false
        )
    }

    @Test
    fun `api documentation for json rpc method without return`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/method.testMethodWithoutResult")
            as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("[JSON-RPC] method"),
            "summary" to "testMethodWithoutResult",
            "operationId" to "testMethodWithoutResultUsingPOST",
            "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "parameters" to listOf(mapOf(
                "in" to "body",
                "name" to "input",
                "description" to "input",
                "required" to true,
                "schema" to mapOf(
                    "type" to "string",
                    "format" to "uuid"
                )
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
    fun `api documentation for java json rpc method without parameters`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/method.testJavaMethodWithoutInput")
            as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("[JSON-RPC] method"),
            "summary" to "testJavaMethodWithoutInput",
            "operationId" to "testJavaMethodWithoutInputUsingPOST",
            "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "responses" to mapOf(
                "200" to mapOf(
                    "description" to "OK",
                    "schema" to mapOf("\$ref" to "#/definitions/TestState")
                )
            ),
            "deprecated" to false
        )
    }

    @Test
    fun `api documentation for java json rpc method without return`() {
        val apiDocs = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        val processMethodInfo = (apiDocs.getValue("paths") as Map<String, Any?>)
            .getValue("/${jsonRpcConfigurationProperties.path}/json-rpc/method.testJavaMethodWithoutResult")
            as Map<String, Any?>

        processMethodInfo["post"] as Map<String, Any?> shouldContainExactly mapOf(
            "tags" to listOf("[JSON-RPC] method"),
            "summary" to "testJavaMethodWithoutResult",
            "operationId" to "testJavaMethodWithoutResultUsingPOST",
            "consumes" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "produces" to listOf(MediaType.APPLICATION_JSON_VALUE),
            "parameters" to listOf(mapOf(
                "in" to "body",
                "name" to "input",
                "description" to "input",
                "required" to true,
                "schema" to mapOf(
                    "type" to "string",
                    "format" to "uuid"
                )
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
