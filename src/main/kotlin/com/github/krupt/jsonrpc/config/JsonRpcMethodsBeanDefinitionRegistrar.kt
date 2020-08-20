package com.github.krupt.jsonrpc.config

import com.github.krupt.jsonrpc.JsonRpcMethod
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.support.BeanDefinitionRegistry
import org.springframework.beans.factory.support.BeanNameGenerator
import org.springframework.boot.autoconfigure.AutoConfigurationPackages
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar
import org.springframework.core.env.Environment
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.filter.AssignableTypeFilter
import java.beans.Introspector

class JsonRpcMethodsBeanDefinitionRegistrar(
    environment: Environment,
    private val beanFactory: BeanFactory
) : ImportBeanDefinitionRegistrar {

    private val candidateComponentProvider =
        ClassPathScanningCandidateComponentProvider(false, environment)
            .apply {
                addIncludeFilter(AssignableTypeFilter(JsonRpcMethod::class.java))
            }

    override fun registerBeanDefinitions(
        importingClassMetadata: AnnotationMetadata,
        registry: BeanDefinitionRegistry,
        importBeanNameGenerator: BeanNameGenerator
    ) {
        AutoConfigurationPackages.get(beanFactory).forEach { `package` ->
            candidateComponentProvider.findCandidateComponents(`package`).forEach {
                registry.registerBeanDefinition(
                    lastPackageAndClassName(
                        importBeanNameGenerator.generateBeanName(it, registry)
                    ),
                    it
                )
            }
        }
    }
}

private fun lastPackageAndClassName(fullClassName: String): String {
    val split = fullClassName.split('.')
    val className = split.last().removeSuffix("Method")
    val lastPackage = split[split.size - 2]

    return "$lastPackage.${Introspector.decapitalize(className)}"
}
