# Auth-Service Java Client

![Java Version](https://img.shields.io/badge/Java-17-blue)
![version](https://img.shields.io/badge/version-1.1.0-blue)

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Changelogs](#changelogs)

### Overview

AuthClient provides a versatile solution for validating auth tokens with support for flexible caching implementation for
performance optimization.<br>
When an auth token is not found in the cache, the library queries the auth service, validates the token, and updates the
cache if needed. AuthClient is fully capable to query multiple auth service instances if one of the instance doesn't
respond back
making it fit for microservice environment.

* [High Level Architecture](docs/authclient_arch_overview.md)

### Key Features

- **Token Validation**: Validate authorization tokens with ease
- **Caching**: Choose between in-memory or Redis-based caching for improved performance
- **Automatic Cache Management**: Tokens are automatically cached and updated as needed
- **Java 17+ Compatibility**: Utilizes the latest features of Java 17

### Installation

AuthClient library can be integrated into Java project using Maven. Add the following dependency to your `pom.xml`:

```xml

<dependency>
    <groupId>com.akgarg</groupId>
    <artifactId>authservice-java-client</artifactId>
    <version>1.1.0</version>
</dependency>
```

Java Version 17 is the baseline java version required.

### Configuration

#### Default Configuration

```java
package foo.bar;

import com.akgarg.client.authclient.AuthClientBuilder;
import com.akgarg.client.authclient.AuthClient;

class AuthClientConfiguration {

    // Initialize AuthClient with default configuration
    void defaultAuthClient() {
        final AuthClient authClient = AuthClientBuilder
                .builder()
                .build();

        // ...
    }
} 
```

Default configuration uses the in-memory cache by default. [Click here](#redis-cache-configuration) to know about
changing cache store to
redis.

#### Redis Cache Configuration

Use following configuration to use AuthClient with Redis as cache store:

```java
package foo.bar;

import com.akgarg.client.authclient.AuthClientBuilder;
import com.akgarg.client.authclient.cache.AuthTokenCacheStrategy;
import com.akgarg.client.authclient.AuthClient;
import com.akgarg.client.authclient.redis.RedisConnectionPoolConfig;
import com.akgarg.client.authclient.redis.RedisConnectionProperty;

import java.time.Duration;

class AuthClientConfiguration {

    // Initialize AuthClient with redis as cache store
    void redisAuthClient() {
        final RedisConnectionProperty connectionProperty = new RedisConnectionProperty("localhost", 6379);
        final RedisConnectionPoolConfig connectionPoolConfig = RedisConnectionPoolConfig.withDefaults();

        final AuthClient authClient = AuthClientBuilder
                .builder()
                .cacheStrategy(AuthTokenCacheStrategy.REDIS)
                .redisConnectionProperties(connectionProperty)
                .redisConnectionPoolConfig(connectionPoolConfig)
                .build();

        // ...
    }
} 
```

### Usage

```java
package foo.bar;

import com.akgarg.client.authclient.AuthClient;
import com.akgarg.client.authclient.common.AuthServiceEndpoint;
import com.akgarg.client.authclient.common.ValidateTokenRequest;

import java.util.List;

public class UsageExample() {

    private final AuthClient authClient;

    public UsageExample(final AuthClient authClient) {
        this.authClient = authClient;
    }

    public void checkTokenValidityExample() {
        final String userId = "user-id-goes-here";
        final String authToken = "auth-token-goes-here";
        final List<AuthServiceEndpoint> endpoints = List.of(
                new AuthServiceEndpoint("http", "localhost", 1234),
                new AuthServiceEndpoint("http", "localhost", 5678)
        );

        final ValidateTokenRequest validateTokenRequest = new ValidateTokenRequest(
                userId,
                authToken,
                endpoints
        );

        final boolean tokenValidationResponse = authClient.validate(tokenValidationRequest);

        // process output accordingly
        System.out.printf("Token validation response: %b%n", tokenValidationResponse);
    }

}
```

### Changelogs

Refer [changelog docs](docs/changelogs.md) for detailed changelogs