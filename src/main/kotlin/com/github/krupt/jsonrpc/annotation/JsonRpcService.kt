package com.github.krupt.jsonrpc.annotation

import org.springframework.core.annotation.AliasFor
import org.springframework.stereotype.Service

/**
 * Indicates that an annotated class is available for call by JsonRpc engine.
 * Also indicates that an annotated class is a [Service]
 *
 * @see Service
 */
@Target(AnnotationTarget.CLASS)
@Retention
@Service
annotation class JsonRpcService(

        /**
         * @see  Service.value
         */
        @Suppress("unused")
        @get:AliasFor(annotation = Service::class)
        val value: String = ""
)
