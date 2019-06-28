package com.github.krupt.jsonrpc.config

import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(JsonRpcProperties::class)
@ComponentScan(JsonRpcProperties.JSON_RPC_BASE_PACKAGE)
class JsonRpcAutoConfiguration {

    @Bean
    fun jacksonKotlinModule() = KotlinModule()
}
