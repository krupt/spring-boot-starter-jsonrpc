package com.github.krupt.jsonrpc.exception.impl

import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.exception.JsonRpcException
import com.github.krupt.jsonrpc.exception.JsonRpcExceptionHandler
import org.slf4j.LoggerFactory

class DefaultJsonRpcExceptionHandler : JsonRpcExceptionHandler {

    companion object {
        private val log = LoggerFactory.getLogger(DefaultJsonRpcExceptionHandler::class.java)
    }

    override fun handle(exception: Throwable) =
            if (exception is JsonRpcException) {
                JsonRpcError(exception.code, exception.message, exception.data)
            } else {
                log.error("Unhandled exception", exception)
                JsonRpcError(JsonRpcError.INTERNAL_ERROR, "Unhandled exception", exception.toString())
            }
}
