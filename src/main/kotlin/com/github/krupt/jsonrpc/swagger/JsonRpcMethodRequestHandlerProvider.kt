package com.github.krupt.jsonrpc.swagger

import com.github.krupt.jsonrpc.JsonRpcMethod
import com.github.krupt.jsonrpc.config.JsonRpcConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import springfox.documentation.RequestHandler
import springfox.documentation.spi.service.RequestHandlerProvider

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("swagger")
class JsonRpcMethodRequestHandlerProvider(
    jsonRpcMethodImpls: Map<String, JsonRpcMethod<*, *>>,
    jsonRpcConfigurationProperties: JsonRpcConfigurationProperties
) : RequestHandlerProvider {

    private val methods = jsonRpcMethodImpls.map {
        JsonRpcMethodRequestHandler(
            jsonRpcConfigurationProperties.path,
            it.key,
            it.value
        )
    }

    override fun requestHandlers(): List<RequestHandler> {
        return methods
    }
}
