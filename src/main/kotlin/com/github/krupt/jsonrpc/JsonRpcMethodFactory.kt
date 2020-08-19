package com.github.krupt.jsonrpc

import com.github.krupt.jsonrpc.annotation.JsonRpcService
import com.github.krupt.jsonrpc.annotation.NoJsonRpcMethod
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.data.util.ProxyUtils
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Modifier

@Component
class JsonRpcMethodFactory(
    beanFactory: ListableBeanFactory
) {

    // Map<methodName, jsonRpcMethodDefinition>
    val methods =
        beanFactory.getBeansWithAnnotation(JsonRpcService::class.java)
            .map {
                ProxyUtils.getUserClass(it.value).methods
                    .filter { method ->
                        Modifier.isPublic(method.modifiers) &&
                            !Modifier.isStatic(method.modifiers) &&
                            method.parameters.size <= 1 &&
                            method.declaringClass != Object::class.java &&
                            !method.isAnnotationPresent(NoJsonRpcMethod::class.java)
                    }.map { method ->
                        "${it.key}.${method.name}" to JsonRpcMethodDefinition(it.key, it.value, method)
                    }
            }.flatten().toMap()
}

data class JsonRpcMethodDefinition(
    val beanName: String,
    val beanInstance: Any,
    val method: Method
)
