package com.github.krupt.jsonrpc.swagger

import com.github.krupt.jsonrpc.JsonRpcServiceMethodFactory
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
class JsonRpcServiceMethodRequestHandlerProvider(
    jsonRpcServiceMethodFactory: JsonRpcServiceMethodFactory,
    jsonRpcConfigurationProperties: JsonRpcConfigurationProperties
) : RequestHandlerProvider {

    private val methods = jsonRpcServiceMethodFactory.methods.map {
        JsonRpcServiceMethodRequestHandler(
            jsonRpcConfigurationProperties.path,
            it.value.beanName,
            it.key,
            it.value.method
        )
    }

    override fun requestHandlers(): List<RequestHandler> {
        return methods
    }
}
