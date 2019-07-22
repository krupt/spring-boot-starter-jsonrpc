package com.github.krupt.jsonrpc.exception

import com.github.krupt.jsonrpc.dto.JsonRpcError

interface JsonRpcExceptionHandler {

    fun handle(exception: Throwable): JsonRpcError
}
