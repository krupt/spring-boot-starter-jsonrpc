package com.github.krupt.jsonrpc.web.rest

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import com.github.krupt.jsonrpc.JsonRpcMethodFactory
import com.github.krupt.jsonrpc.dto.JsonRpcError
import com.github.krupt.jsonrpc.dto.JsonRpcRequest
import com.github.krupt.jsonrpc.dto.JsonRpcResponse
import com.github.krupt.jsonrpc.exception.JsonRpcExceptionHandler
import io.swagger.annotations.ApiOperation
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.Validator
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

@RestController
class JsonRpcController(
        jsonRpcMethodFactory: JsonRpcMethodFactory,
        private val exceptionHandler: JsonRpcExceptionHandler,
        private val objectMapper: ObjectMapper,
        private val validator: Validator
) {

    @Suppress("UNCHECKED_CAST")
    private val methods =
            jsonRpcMethodFactory.methods.mapValues {
                val (_, instance, method) = it.value

                // For best performance
                method.isAccessible = true

                MethodInvocation(
                        method.parameters.firstOrNull()?.type as Class<Any>?,
                        method,
                        instance
                )
            }

    @PostMapping("\${spring.jsonrpc.path:}")
    @ApiOperation(
            "The endpoint that handles all JSON-RPC requests",
            notes = """Read more about <a href="https://www.jsonrpc.org/specification">JSON-RPC 2.0 Specification</a>"""
    )
    fun handle(@RequestBody @Validated request: JsonRpcRequest<Any>): ResponseEntity<JsonRpcResponse<Any>?> {
        var error: JsonRpcError? = null
        var result: Any? = null
        methods[request.method]?.let { method ->
            try {
                if (request.params != null || method.inputType == null) {
                    val params = request.params?.let {
                        objectMapper.convertValue(it, method.inputType)
                    }
                    // Validate
                    val bindException = params?.let {
                        BindException(params, method.inputType!!.simpleName)
                    }
                    bindException?.run {
                        validator.validate(params, bindException)
                    }
                    if (bindException?.hasErrors() == true) {
                        error = JsonRpcError(
                                JsonRpcError.INVALID_PARAMS,
                                JsonRpcError.INVALID_PARAMS_MESSAGE,
                                bindException.bindingResult.fieldErrors
                                        .map { fieldError ->
                                            fieldError.toString()
                                        }
                        )
                    } else {
                        try {
                            result = method.invoke(params)
                        } catch (e: Throwable) {
                            val exception = if (e is InvocationTargetException) {
                                e.cause!!
                            } else {
                                e
                            }
                            error = exceptionHandler.handle(exception)
                        }
                    }
                } else {
                    error = JsonRpcError(JsonRpcError.INVALID_PARAMS, JsonRpcError.INVALID_PARAMS_MESSAGE, "Params can't be null")
                }
            } catch (e: Exception) {
                error = JsonRpcError(JsonRpcError.INVALID_PARAMS, JsonRpcError.INVALID_PARAMS_MESSAGE, e.toString())
            }
        } ?: run {
            error = JsonRpcError(JsonRpcError.METHOD_NOT_FOUND, JsonRpcError.METHOD_NOT_FOUND_MESSAGE)
        }

        return request.id?.let { id ->
            ResponseEntity.ok(JsonRpcResponse(id, result, error))
        } ?: ResponseEntity.ok().build()
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleJsonParseException(
            exception: HttpMessageNotReadableException
    ): JsonRpcResponse<Void> =
            when (val cause = exception.cause) {
                is JsonParseException -> JsonRpcResponse(
                        error = JsonRpcError(
                                JsonRpcError.PARSE_ERROR,
                                JsonRpcError.PARSE_ERROR_MESSAGE
                        )
                )
                is MissingKotlinParameterException -> JsonRpcResponse(
                        // TODO extract and pass here id from request
                        error = JsonRpcError(
                                JsonRpcError.INVALID_REQUEST,
                                JsonRpcError.INVALID_REQUEST_MESSAGE,
                                cause.msg
                        )
                )
                else -> JsonRpcResponse(
                        error = JsonRpcError(
                                JsonRpcError.INTERNAL_ERROR,
                                "Internal error"
                        )
                )
            }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(exception: MethodArgumentNotValidException) =
            JsonRpcResponse<Void>(
                    id = (exception.bindingResult.target as JsonRpcRequest<*>).id ?: 1,
                    error = JsonRpcError(
                            JsonRpcError.INVALID_REQUEST,
                            "Invalid request",
                            exception.bindingResult.fieldErrors
                                    .map {
                                        it.toString()
                                    }
                    )
            )
}

data class MethodInvocation(
        val inputType: Class<Any>?,
        private val method: Method,
        private val instance: Any
) {

    fun invoke(args: Any?): Any? = if (inputType != null) {
        method.invoke(instance, args)
    } else {
        method.invoke(instance)
    }
}
