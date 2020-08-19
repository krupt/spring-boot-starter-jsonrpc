package com.github.krupt.jsonrpc.config

import com.google.common.base.Predicates
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import springfox.documentation.builders.PathSelectors
import springfox.documentation.builders.RequestHandlerSelectors
import springfox.documentation.spi.DocumentationType
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder
import springfox.documentation.spring.web.plugins.Docket
import springfox.documentation.swagger2.annotations.EnableSwagger2

@Configuration
@Profile("!prod")
@EnableSwagger2
class JsonRpcSwaggerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Docket::class, ApiSelectorBuilder::class)
    fun docketAuto(jsonRpcConfigurationProperties: JsonRpcConfigurationProperties): Docket =
        Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .paths(PathSelectors.any())
            .apis(
                Predicates.or(
                    RequestHandlerSelectors.basePackage(JsonRpcConfigurationProperties.JSON_RPC_BASE_PACKAGE),
                    RequestHandlerSelectors.basePackage(jsonRpcConfigurationProperties.basePackage!!)
                )
            ).build()

    @Bean
    @ConditionalOnMissingBean(Docket::class)
    @ConditionalOnBean(ApiSelectorBuilder::class)
    fun docket(
        apiSelectorBuilder: ApiSelectorBuilder,
        jsonRpcConfigurationProperties: JsonRpcConfigurationProperties
    ): Docket =
        apiSelectorBuilder
            .apis(
                Predicates.or(
                    RequestHandlerSelectors.basePackage(JsonRpcConfigurationProperties.JSON_RPC_BASE_PACKAGE),
                    RequestHandlerSelectors.basePackage(jsonRpcConfigurationProperties.basePackage!!)
                )
            ).build()
}
