package com.github.krupt.test.method;

import com.github.krupt.jsonrpc.JsonRpcMethod;
import com.github.krupt.test.model.TestState;

public class TestJavaMethodWithoutInput implements JsonRpcMethod<Void, TestState> {

    @Override
    public TestState invoke(Void input) {
        return new TestState("Test");
    }
}
