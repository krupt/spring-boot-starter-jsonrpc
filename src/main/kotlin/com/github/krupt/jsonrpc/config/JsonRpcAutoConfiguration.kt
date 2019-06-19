package com.github.krupt.jsonrpc.config

import com.google.common.base.Predicates
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@EnableConfigurationProperties(JsonRpcProperties::class)
@EnableSwagger2
class JsonRpcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Docket::class, ApiSelectorBuilder::class)
    fun docketAuto(jsonRpcProperties: JsonRpcProperties) = Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .paths(PathSelectors.any())
            .apis(
                    Predicates.or(
                            RequestHandlerSelectors.basePackage("com.github.krupt.jsonrpc"),
                            RequestHandlerSelectors.basePackage(jsonRpcProperties.basePackage)
                    )
            ).build()

    @Bean
    @ConditionalOnMissingBean(Docket::class)
    @ConditionalOnBean(ApiSelectorBuilder::class)
    fun docket(
            apiSelectorBuilder: ApiSelectorBuilder,
            jsonRpcProperties: JsonRpcProperties
    ) = apiSelectorBuilder
            .apis(
                    Predicates.or(
                            RequestHandlerSelectors.basePackage("com.github.krupt.jsonrpc"),
                            RequestHandlerSelectors.basePackage(jsonRpcProperties.basePackage)
                    )
            ).build()

    @Bean
    @ConditionalOnMissingBean
    fun swaggerUiJsonRpcExtensionAuto() = object : WebMvcConfigurer {
        override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
            registry.addResourceHandler("/swagger-ui.html")
                    .addResourceLocations("classpath:/static/swagger-ui.html")

            registry.addResourceHandler("/static/**")
                    .addResourceLocations("classpath:/static/")
        }
    }

    @Bean
    @ConditionalOnBean
    fun swaggerUiJsonRpcExtension(webMvcConfigurer: WebMvcConfigurer) =
            object : WebMvcConfigurer by webMvcConfigurer {
                override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
                    registry.addResourceHandler("/swagger-ui.html")
                            .addResourceLocations("classpath:/static/swagger-ui.html")

                    registry.addResourceHandler("/static/**")
                            .addResourceLocations("classpath:/static/")

                    super.addResourceHandlers(registry)
                }
            }
}
