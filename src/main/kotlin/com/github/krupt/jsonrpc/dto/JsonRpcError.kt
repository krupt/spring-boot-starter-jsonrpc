package com.github.krupt.jsonrpc.dto

data class JsonRpcError(
        val code: Int = 0,
        val message: String,
        val data: Any? = null
) {

    /**
     * Default JSON-RPC error codes
     */
    companion object {
        const val PARSE_ERROR = -32700
        const val INVALID_REQUEST = -32600
        const val METHOD_NOT_FOUND = -32601
        const val INVALID_PARAMS = -32602
        const val INTERNAL_ERROR = -32603

        const val PARSE_ERROR_MESSAGE = "Parse error"
        const val INVALID_REQUEST_MESSAGE = "Invalid request"
        const val METHOD_NOT_FOUND_MESSAGE = "Method not found"
        const val INVALID_PARAMS_MESSAGE = "Invalid method parameter(s)"
    }
}
