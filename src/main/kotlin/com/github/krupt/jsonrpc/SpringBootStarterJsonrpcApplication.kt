package com.github.krupt.jsonrpc

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringBootStarterJsonrpcApplication

fun main(args: Array<String>) {
	runApplication<SpringBootStarterJsonrpcApplication>(*args)
}
