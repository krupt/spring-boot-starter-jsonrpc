package com.github.krupt.test.exception

import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.exception.impl.DefaultJsonRpcExceptionHandler
import org.springframework.stereotype.Component

@Component
class ReThrowingJsonRpcExceptionHandler : DefaultJsonRpcExceptionHandler() {

    override fun handleNonJsonRpcException(exception: Throwable): JsonRpcError? {
        if (exception is ReThrowingException) {
            throw exception
        } else {
            return super.handleNonJsonRpcException(exception)
        }
    }
}
