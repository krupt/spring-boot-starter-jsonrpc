[![Maven Central](https://img.shields.io/maven-central/v/com.github.krupt/spring-boot-starter-jsonrpc.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.krupt/spring-boot-starter-jsonrpc)

# Spring Boot Starter JSON-RPC
The primary goal of the Spring Boot Starter JSON-RPC project is to make it easier to build Spring-powered applications that use [JSON-RPC 2.0 Specification](https://www.jsonrpc.org/specification).

## Features ##

* Exports beans' methods to make it available from JSON-RPC API
* Generates Swagger's documentation on available methods through JSON-RPC API
* Gives opportunity to test JSON-RPC methods with raw methods' params using SwaggerUI
* Easy Spring integration

## Restrictions ##
* Works only for Spring Web (Servlet), doesn't work with Spring Web Flux
* Allows methods with one or none parameters
* JSON-RPC's batch requests are not allowed

## Quick Start ##
Download the jar through Maven:

```xml
<dependency>
  <groupId>com.github.krupt</groupId>
  <artifactId>spring-boot-starter-jsonrpc</artifactId>
  <version>${version}</version>
</dependency>
```

Add properties to your application:

```yml
spring.jsonrpc:
    path: api
    basePackage: com.github.krupt
```
where:
- `path` is the HTTP URL path that is the endpoint of JSON-RPC engine(for example: http://localhost:8080/api)
- `basePackage` is a base package's name to export all your beans' methods for Swagger

The simple JSON-RPC service looks like this: 
```java
@JsonRpcService
public class UserService {

    public User get(UUID userId) {
        ...
    }
}

```

All the public methods with one or none parameters are collected by JSON RPC Engine and can be accessed through HTTP API. If you want to hide beans' public methods, you need to mark the target method with `@NoJsonRpcMethod` annotation.

If you want specific exception handling, add bean that implements com.github.krupt.jsonrpc.exception.JsonRpcExceptionHandler.

---

There are some Gradle projects that demonstrate typical use cases with and features available in the Spring Boot JSON-RPC Starter:
* [Kotlin](https://github.com/krupt/spring-boot-starter-jsonrpc-example)
* [Java](https://github.com/krupt/spring-boot-starter-jsonrpc-example-java)

In these projects Swagger's documentation looks like this:

![Swagger's documentation](https://github.com/krupt/spring-boot-starter-jsonrpc/raw/master/images/methods.png)

![Information of JSON-RPC method](https://github.com/krupt/spring-boot-starter-jsonrpc/raw/master/images/method_params.png)

![Trying JSON-RPC method](https://github.com/krupt/spring-boot-starter-jsonrpc/raw/master/images/method_trying.png)

---

## Road map ##
* Add support for Spring Web Flux
* Better Spring integration (like Spring WebMVC)
