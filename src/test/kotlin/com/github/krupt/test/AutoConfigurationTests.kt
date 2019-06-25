package com.github.krupt.test

import com.ninjasquad.springmockk.MockkBean
import io.kotlintest.matchers.maps.shouldContainExactly
import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForObject
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Bean
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors
import springfox.documentation.service.ApiInfo
import springfox.documentation.service.Contact
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder
import springfox.documentation.spring.web.plugins.Docket

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class AutoConfigurationTests {

    @MockkBean(relaxed = true)
    private lateinit var testRunnable: Runnable

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var restTemplate: TestRestTemplate

    @TestConfiguration
    class TestSwaggerConfiguration {

        @Bean
        fun docketApiSelectionBuilder(): ApiSelectorBuilder =
                Docket(DocumentationType.SWAGGER_2)
                        .apiInfo(ApiInfo(
                                "Test title",
                                "Test description",
                                "Test version",
                                "Test terms",
                                Contact(
                                        "krupt",
                                        "https://github.com/krupt",
                                        "krupt25@gmail.com"
                                ),
                                "Apache 2.0",
                                null,
                                emptyList()
                        ))
                        .useDefaultResponseMessages(false)
                        .select()
                        .paths(PathSelectors.any())

        @Bean
        fun webMvcConfigurer() = object : WebMvcConfigurer {
            override fun addResourceHandlers(registry: ResourceHandlerRegistry) {

                registry.addResourceHandler("/assets/**")
                        .addResourceLocations("classpath:/assets/")
            }
        }
    }

    @Test
    fun `Application starts and returns custom swagger docket info`() {
        val response = restTemplate.getForObject<Map<String, Any?>>("http://localhost:$port/v2/api-docs")!!
        response["info"] as Map<String, Any?> shouldContainExactly mapOf(
                "title" to "Test title",
                "description" to "Test description",
                "version" to "Test version",
                "termsOfService" to "Test terms",
                "license" to mapOf(
                        "name" to "Apache 2.0"
                ),
                "contact" to mapOf(
                        "name" to "krupt",
                        "url" to "https://github.com/krupt",
                        "email" to "krupt25@gmail.com"
                )
        )
    }

    @Test
    fun `Application starts and returns swagger page`() {
        restTemplate.getForObject<String>(
                "http://localhost:$port/swagger-ui.html"
        )!! shouldContain
                """<script src="static/swagger-json-rpc-plugin.js"> </script>"""
    }

    @Test
    fun `Application starts and returns custom static resources`() {
        restTemplate.getForObject<String>(
                "http://localhost:$port/assets/test.js"
        )!! shouldBe """
            window.onload = function() {
                console.log("Test");
            }
            
        """.trimIndent()
    }
}
