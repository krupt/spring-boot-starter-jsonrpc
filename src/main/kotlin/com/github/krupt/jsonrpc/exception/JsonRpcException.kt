package com.github.krupt.jsonrpc.exception

open class JsonRpcException(
        val code: String,
        val data: Any? = null
) : RuntimeException("JsonRpcError with code=$code"
        + (if (data == null) "" else " and data=$data")
)
