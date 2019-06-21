package com.github.krupt.jsonrpc.exception

open class JsonRpcException(
        /**
         * Codes between -32768 and -32000 are reserved by JSON-RPC protocol
         */
        val code: Int,
        override val message: String,
        val data: Any? = null
) : RuntimeException("JsonRpcError with code=$code, message=$message"
        + (if (data == null) "" else " and data=$data")
)
