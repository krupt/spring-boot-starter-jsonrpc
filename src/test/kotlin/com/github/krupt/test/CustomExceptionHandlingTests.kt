package com.github.krupt.test

import com.github.krupt.jsonrpc.config.JsonRpcConfigurationProperties
import com.github.krupt.jsonrpc.dto.JsonRpcRequest
import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpStatus

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomExceptionHandlingTests {

    @MockkBean(relaxed = true)
    private lateinit var testRunnable: Runnable

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var jsonRpcConfigurationProperties: JsonRpcConfigurationProperties

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @Test
    fun `test rethrow`() {
        val rawResponse = restTemplate.postForEntity<Any>(
            "http://localhost:$port/${jsonRpcConfigurationProperties.path}",
            JsonRpcRequest(
                123,
                "testService.reThrowingException",
                null,
                "2.0"
            )
        )

        println(rawResponse.body)
        rawResponse.statusCode shouldBe HttpStatus.NOT_FOUND
    }
}
