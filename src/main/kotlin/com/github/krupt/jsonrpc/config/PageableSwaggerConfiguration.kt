package com.github.krupt.jsonrpc.config

import com.fasterxml.classmate.TypeResolver
import io.swagger.annotations.ApiModelProperty
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.Ordered
import org.springframework.data.domain.Pageable
import springfox.documentation.builders.AlternateTypeBuilder
import springfox.documentation.builders.AlternateTypePropertyBuilder
import springfox.documentation.schema.AlternateTypeRules
import springfox.documentation.spring.web.plugins.Docket

@Configuration
@ConditionalOnClass(Pageable::class)
@Profile("!prod")
class PageableSwaggerConfiguration(
    private val docket: Docket,
    private val typeResolver: TypeResolver
) : InitializingBean {

    override fun afterPropertiesSet() {
        docket.alternateTypeRules(
            AlternateTypeRules.newRule(
                typeResolver.resolve(Pageable::class.java),
                pageableType(),
                Ordered.HIGHEST_PRECEDENCE
            )
        )
    }

    private fun pageableType() =
        AlternateTypeBuilder()
            .fullyQualifiedClassName(
                "${Pageable::class.java.`package`.name}.swagger.${Pageable::class.java.simpleName}"
            )
            .property(
                AlternateTypePropertyBuilder()
                    .withName("page")
                    .withType(Int::class.java)
                    .withCanRead(true)
                    .withCanWrite(true)
            ).property(
                AlternateTypePropertyBuilder()
                    .withName("size")
                    .withType(Int::class.java)
                    .withCanRead(true)
                    .withCanWrite(true)
            ).property(
                AlternateTypePropertyBuilder()
                    .withName("sort")
                    .withType(SortList::class.java)
                    .withCanRead(true)
                    .withCanWrite(true)
            )
            .build()
}

abstract class SortList : List<Sort>

@Suppress("unused")
class Sort {

    lateinit var property: String

    @ApiModelProperty(required = false)
    var direction: org.springframework.data.domain.Sort.Direction = org.springframework.data.domain.Sort.Direction.ASC
}
