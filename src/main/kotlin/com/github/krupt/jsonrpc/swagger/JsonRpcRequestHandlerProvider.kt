package com.github.krupt.jsonrpc.swagger

import com.github.krupt.jsonrpc.JsonRpcMethodFactory
import com.github.krupt.jsonrpc.config.JsonRpcConfigurationProperties
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import springfox.documentation.RequestHandler
import springfox.documentation.spi.service.RequestHandlerProvider

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Profile("!prod")
class JsonRpcRequestHandlerProvider(
    jsonRpcMethodFactory: JsonRpcMethodFactory,
    jsonRpcConfigurationProperties: JsonRpcConfigurationProperties
) : RequestHandlerProvider {

    private val methods = jsonRpcMethodFactory.methods.map {
        JsonRpcRequestHandler(jsonRpcConfigurationProperties.path, it.value.beanName, it.key, it.value.method)
    }

    override fun requestHandlers(): List<RequestHandler> {
        return methods
    }
}
