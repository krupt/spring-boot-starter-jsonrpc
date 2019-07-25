package com.github.krupt.jsonrpc.swagger

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.google.common.base.Optional
import org.springframework.core.annotation.AnnotationUtils
import org.springframework.http.MediaType
import org.springframework.util.ClassUtils
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ValueConstants
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.mvc.condition.NameValueExpression
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import springfox.documentation.RequestHandler
import springfox.documentation.RequestHandlerKey
import springfox.documentation.service.ResolvedMethodParameter
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.UUID

class JsonRpcRequestHandler(
        private val basePath: String,
        private val beanName: String,
        private val methodName: String,
        private val method: Method
) : RequestHandler {

    companion object {
        private val typeResolver = TypeResolver()
        private val requestBodyAnnotation =
                Proxy.newProxyInstance(
                        RequestBody::class.java.classLoader,
                        arrayOf(RequestBody::class.java)
                ) { _, method, _ ->
                    if (method.name == "required") {
                        true
                    } else {
                        null
                    }
                } as RequestBody

        private val requestParamAnnotation =
                Proxy.newProxyInstance(
                        RequestParam::class.java.classLoader,
                        arrayOf(RequestParam::class.java)
                ) { _, method, _ ->
                    when (method.name) {
                        "required" -> true
                        "name", "value" -> ""
                        "defaultValue" -> ValueConstants.DEFAULT_NONE
                        else -> null
                    }
                } as RequestParam
    }

    override fun isAnnotatedWith(annotation: Class<out Annotation>) =
            AnnotationUtils.findAnnotation(method, annotation) != null

    override fun getPatternsCondition() =
            PatternsRequestCondition("/$basePath/json-rpc/$methodName")

    override fun groupName() = "[JSON-RPC] $beanName"

    override fun getName(): String = method.name

    override fun supportedMethods() = setOf(RequestMethod.POST)

    override fun produces() = setOf(MediaType.APPLICATION_JSON)

    override fun consumes() = setOf(MediaType.APPLICATION_JSON)

    override fun headers(): Set<NameValueExpression<String>> = emptySet()

    override fun params(): Set<NameValueExpression<String>> = emptySet()

    override fun <T : Annotation> findAnnotation(annotation: Class<T>): Optional<T> =
            Optional.fromNullable(AnnotationUtils.findAnnotation(method, annotation))

    override fun key() = RequestHandlerKey(
            patternsCondition.patterns,
            supportedMethods(),
            consumes(),
            produces()
    )

    override fun getParameters() =
            method.parameters.mapIndexed { index, it ->
                ResolvedMethodParameter(
                        index,
                        it.name,
                        it.annotations.asList()
                                + if (isSimpleParameter(it.type)) requestParamAnnotation else requestBodyAnnotation,
                        typeResolver.resolve(it.type)
                )
            }

    override fun getReturnType(): ResolvedType =
            typeResolver.resolve(method.genericReturnType)

    override fun <T : Annotation> findControllerAnnotation(annotation: Class<T>): Optional<T> =
            Optional.fromNullable(AnnotationUtils.findAnnotation(method.declaringClass, annotation))

    override fun declaringClass(): Class<*> = method.declaringClass

    override fun toString() =
            "JsonRpcMethod($methodName)"

    override fun getRequestMapping(): RequestMappingInfo {
        throw NotImplementedError("Deprecated")
    }

    override fun getHandlerMethod(): HandlerMethod {
        throw NotImplementedError("Deprecated")
    }

    override fun combine(other: RequestHandler?) = this

    private fun isSimpleParameter(clazz: Class<*>): Boolean {
        return ClassUtils.isPrimitiveOrWrapper(clazz)
                || clazz == String::class.java
                || clazz == UUID::class.java
    }
}
