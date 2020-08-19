package com.github.krupt.jsonrpc.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class JsonRpcResponse<T>(

    val id: Any = 1,

    val result: T? = null,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val error: JsonRpcError? = null,

    @JsonProperty("jsonrpc")
    val jsonRpc: String = "2.0"
)
