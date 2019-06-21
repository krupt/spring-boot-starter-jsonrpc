package com.github.krupt.jsonrpc.dto

import com.fasterxml.jackson.annotation.JsonProperty
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern

data class JsonRpcRequest(
        val id: Any?,
        @JsonProperty("jsonrpc")
        @Pattern(regexp = "2\\.0")
        val jsonRpc: String,
        @NotBlank
        val method: String,
        val params: Map<String, Any?>?
)
