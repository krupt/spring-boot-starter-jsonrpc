package com.github.krupt.jsonrpc.dto

data class JsonRpcRequest(
        val id: Long?,
        val method: String,
        val params: Map<String, Any?>
)
