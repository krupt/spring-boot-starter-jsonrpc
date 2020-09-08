package com.github.krupt.test.config

import com.github.krupt.jsonrpc.JsonRpcMethod
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class JsonRpcMethodsBeanDefinitionRegistrarTest {

    @Autowired
    private lateinit var methods: Map<String, JsonRpcMethod<*, *>>

    @Test
    fun test() {
        methods.keys shouldContainExactlyInAnyOrder setOf(
            "method.test",
            "method.testMethodWithoutResult",
            "method.testMethodWithoutInput",
            "method.testMethodWithException"
        )
    }
}
