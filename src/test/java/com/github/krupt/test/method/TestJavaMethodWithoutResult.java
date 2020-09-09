package com.github.krupt.test.method;

import com.github.krupt.jsonrpc.JsonRpcMethod;

import java.util.UUID;

public class TestJavaMethodWithoutResult implements JsonRpcMethod<UUID, Void> {

    @Override
    public Void invoke(UUID input) {
        return null;
    }
}
