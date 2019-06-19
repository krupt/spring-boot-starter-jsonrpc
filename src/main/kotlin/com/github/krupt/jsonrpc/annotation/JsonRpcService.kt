package com.github.krupt.jsonrpc.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Service

@Target(AnnotationTarget.CLASS)
@Retention
@Service
annotation class JsonRpcService(
        @get:AliasFor(annotation = Service::class)
        val value: String = ""
)
