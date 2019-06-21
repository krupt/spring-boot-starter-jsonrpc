package com.github.krupt.jsonrpc.web.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.krupt.jsonrpc.JsonRpcMethodFactory
import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.dto.JsonRpcRequest
import com.github.krupt.jsonrpc.dto.JsonRpcResponse
import com.github.krupt.jsonrpc.exception.JsonRpcException
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@RestController
class JsonRpcController(
        jsonRpcMethodConfiguration: JsonRpcMethodFactory,
        private val objectMapper: ObjectMapper
) {

    companion object {
        private val log = LoggerFactory.getLogger(JsonRpcController::class.java)
    }

    @Suppress("UNCHECKED_CAST")
    private val methods =
            jsonRpcMethodConfiguration.methods.mapValues {
                val (_, method) = it.value

                // For best performance
                method.isAccessible = true

                MethodInvocation(
                        method.parameters[0].type as Class<Any>,
                        method,
                        it.value
                )
            }

    @PostMapping("\${spring.jsonrpc.path}")
    fun handle(@RequestBody @Validated request: JsonRpcRequest): ResponseEntity<JsonRpcResponse?> {
        var error = JsonRpcError()
        var result: Any? = null
        methods[request.method]?.let {
            try {
                val params = objectMapper.convertValue(request.params, it.inputType)
                try {
                    log.debug("Request: {}", params)
                    result = it.invoke(params)
                    log.debug("Result: {}", result)
                } catch (e: Exception) {
                    val exception = if (e is InvocationTargetException) {
                        e.cause
                    } else {
                        e
                    }
                    error = if (exception is JsonRpcException) {
                        JsonRpcError(exception.code, exception.message, exception.data)
                    } else {
                        log.error("Unhandled exception", exception)
                        JsonRpcError(JsonRpcError.INTERNAL_ERROR, "unhandled_exception", exception.toString())
                    }
                }
            } catch (e: Exception) {
                log.error("Parse error", e)
                error = JsonRpcError(JsonRpcError.INVALID_REQUEST, "invalid_request", e.toString())
            }
        } ?: run {
            error = JsonRpcError(JsonRpcError.METHOD_NOT_FOUND, "method_not_found")
        }

        return request.id?.let { id ->
            ResponseEntity.ok(JsonRpcResponse(id, result, error))
        } ?: ResponseEntity.ok().build()
    }

/*@ExceptionHandler
fun handleJsonParseExceptions() {

}*/
}

data class MethodInvocation(
        val inputType: Class<Any>,
        private val method: Method,
        private val instance: Any
) {

    fun invoke(args: Any): Any? = method.invoke(instance, args)
}
