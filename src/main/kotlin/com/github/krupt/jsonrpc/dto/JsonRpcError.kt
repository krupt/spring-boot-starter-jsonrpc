package com.github.krupt.jsonrpc.dto

data class JsonRpcError(
        val code: String,
        val data: Any? = null
)
