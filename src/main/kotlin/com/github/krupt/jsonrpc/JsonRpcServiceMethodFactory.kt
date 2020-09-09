package com.github.krupt.jsonrpc

import com.github.krupt.jsonrpc.annotation.JsonRpcService
import com.github.krupt.jsonrpc.annotation.NoJsonRpcMethod
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import java.lang.reflect.Modifier

@Component
class JsonRpcServiceMethodFactory(
    beanFactory: ListableBeanFactory
) {

    // Map<methodName, jsonRpcMethodDefinition>
    val methods =
        beanFactory.getBeansWithAnnotation(JsonRpcService::class.java)
            .map {
                AopUtils.getTargetClass(it.value).methods
                    .filter { method ->
                        Modifier.isPublic(method.modifiers) &&
                            !Modifier.isStatic(method.modifiers) &&
                            method.parameters.size <= 1 &&
                            method.declaringClass != Object::class.java &&
                            !method.isAnnotationPresent(NoJsonRpcMethod::class.java)
                    }.map { method ->
                        "${it.key}.${method.name}" to JsonRpcServiceMethodDefinition(it.key, it.value, method)
                    }
            }.flatten().toMap()
}

data class JsonRpcServiceMethodDefinition(
    val beanName: String,
    val beanInstance: Any,
    val method: Method
)
