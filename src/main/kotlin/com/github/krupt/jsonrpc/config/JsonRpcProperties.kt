package com.github.krupt.jsonrpc.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("spring.jsonrpc")
class JsonRpcProperties {

    companion object {
        const val JSON_RPC_BASE_PACKAGE = "com.github.krupt.jsonrpc"
    }

    var path: String = ""
    var basePackage: String? = null
}
