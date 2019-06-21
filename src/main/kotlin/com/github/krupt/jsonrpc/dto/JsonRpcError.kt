package com.github.krupt.jsonrpc.dto

data class JsonRpcError(
        val code: Int = 0,
        val message: String? = null,
        val data: Any? = null
) {

    /**
     * Default JSON-RPC error codes
     */
    @Suppress("unused")
    companion object {
        const val PARSE_ERROR = -32700
        const val INVALID_REQUEST = -32600
        const val METHOD_NOT_FOUND = -32601
        const val INVALID_PARAMS = -32602
        const val INTERNAL_ERROR = -32603
    }
}
