package com.github.krupt.jsonrpc.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.jsonrpc")
class JsonRpcProperties {

    lateinit var path: String
    lateinit var basePackage: String
}
