package com.github.krupt.jsonrpc.swagger

import com.github.krupt.jsonrpc.JsonRpcMethod
import com.google.common.base.Optional
import org.springframework.core.annotation.AnnotationUtils

class JsonRpcMethodRequestHandler(
    basePath: String,
    private val fullMethodName: String,
    private val instance: JsonRpcMethod<*, *>
) : JsonRpcServiceMethodRequestHandler(
    basePath,
    fullMethodName.split('.').first(),
    fullMethodName,
    instance.javaClass.methods.first {
        it.name == "invoke" && !it.isBridge && !it.isSynthetic
    }
) {

    override fun getName(): String = fullMethodName.split('.').last()

    override fun <T : Annotation> findControllerAnnotation(annotation: Class<T>): Optional<T> =
        Optional.fromNullable(AnnotationUtils.findAnnotation(instance.javaClass, annotation))

    override fun declaringClass(): Class<*> = instance.javaClass
}
