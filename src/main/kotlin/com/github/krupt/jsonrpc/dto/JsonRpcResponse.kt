package com.github.krupt.jsonrpc.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JsonRpcResponse(
        val id: Any,
        val result: Any? = null,
        val error: JsonRpcError,
        @JsonProperty("jsonrpc")
        val jsonRpc: String = "2.0"
)
