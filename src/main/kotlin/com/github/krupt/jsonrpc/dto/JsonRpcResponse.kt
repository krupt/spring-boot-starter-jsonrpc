package com.github.krupt.jsonrpc.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class JsonRpcResponse<T>(
        val id: Any = 1,
        val result: T? = null,
        val error: JsonRpcError? = null,
        @JsonProperty("jsonrpc")
        val jsonRpc: String = "2.0"
)
