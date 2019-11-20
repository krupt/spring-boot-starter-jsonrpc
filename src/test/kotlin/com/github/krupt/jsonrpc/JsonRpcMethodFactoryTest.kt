package com.github.krupt.jsonrpc

import com.github.krupt.jsonrpc.annotation.JsonRpcService
import com.github.krupt.jsonrpc.annotation.NoJsonRpcMethod
import com.github.krupt.test.dto.TestRequest
import io.kotlintest.matchers.maps.shouldContainKey
import io.kotlintest.matchers.maps.shouldNotContainKey
import io.kotlintest.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.UUID

@SpringBootTest(classes = [
    JsonRpcMethodFactory::class,
    ServiceWithoutMethods::class,
    ServiceWithOnlyHiddenMethods::class,
    TestServiceWithCustomComponentName::class
])
internal class JsonRpcMethodFactoryTest {

    @Autowired
    private lateinit var jsonRpcMethodFactory: JsonRpcMethodFactory

    @Test
    fun `factory doesn't collect hidden methods`() {
        jsonRpcMethodFactory.methods shouldNotContainKey "serviceWithOnlyHiddenMethods.test"
    }

    @Test
    fun `factory doesn't fail when collecting services without suitable methods`() {
        jsonRpcMethodFactory.methods.size shouldBe 1
        jsonRpcMethodFactory.methods shouldContainKey "customTestService.test"
    }
}

@JsonRpcService
class ServiceWithoutMethods

@JsonRpcService
class ServiceWithOnlyHiddenMethods {

    @NoJsonRpcMethod
    fun hidden(request: TestRequest) {
    }

    private fun internal(request: TestRequest) {
    }
}

@JsonRpcService("customTestService")
class TestServiceWithCustomComponentName {

    companion object {
        val CONSTANT: UUID = UUID.randomUUID()
    }

    fun test(request: TestRequest) {
        run {
            CONSTANT
        }
    }
}
