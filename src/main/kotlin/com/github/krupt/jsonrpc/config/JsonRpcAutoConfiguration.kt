package com.github.krupt.jsonrpc.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.github.krupt.jsonrpc.exception.JsonRpcExceptionHandler
import com.github.krupt.jsonrpc.exception.impl.DefaultJsonRpcExceptionHandler
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@EnableConfigurationProperties(JsonRpcConfigurationProperties::class)
@ComponentScan(JsonRpcConfigurationProperties.JSON_RPC_BASE_PACKAGE)
@Import(JsonRpcMethodsBeanDefinitionRegistrar::class)
class JsonRpcAutoConfiguration {

    @Bean
    fun jacksonKotlinModule() = KotlinModule()

    @Bean
    @ConditionalOnMissingBean(JsonRpcExceptionHandler::class)
    fun defaultJsonRpcExceptionHandler() = DefaultJsonRpcExceptionHandler()
}
