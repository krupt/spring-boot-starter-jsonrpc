package com.github.krupt.test.exception

import com.github.krupt.jsonrpc.exception.JsonRpcException
import com.github.krupt.test.model.TestState

class TestException(
    state: TestState
) : JsonRpcException(
    -29345,
    "Test state is incorrect",
    state
)
