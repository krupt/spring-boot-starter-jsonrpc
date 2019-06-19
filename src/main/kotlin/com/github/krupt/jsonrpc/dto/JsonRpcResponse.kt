package com.github.krupt.jsonrpc.dto

data class JsonRpcResponse(
        val id: Long,
        val result: Any? = null,
        val error: JsonRpcError? = null
)
