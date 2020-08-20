package com.github.krupt.jsonrpc

import org.springframework.stereotype.Indexed

@Indexed
interface JsonRpcMethod<T, R> {

    fun invoke(input: T): R
}
