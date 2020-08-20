package com.github.krupt.test.method

import com.github.krupt.jsonrpc.JsonRpcMethod
import com.github.krupt.test.exception.TestException
import com.github.krupt.test.model.TestState
import java.util.UUID

class TestMethod : JsonRpcMethod<UUID, TestState> {

    override fun invoke(input: UUID) =
        TestState(input.toString())
}

class TestMethodWithoutResult : JsonRpcMethod<UUID, Unit> {

    override fun invoke(input: UUID) {
        // Nothing
    }
}

class TestMethodWithoutInput : JsonRpcMethod<Unit, TestState> {

    override fun invoke(input: Unit) = TestState("Test")
}

class TestMethodWithException : JsonRpcMethod<String, Unit> {

    override fun invoke(input: String) {
        throw TestException(TestState(input))
    }
}
