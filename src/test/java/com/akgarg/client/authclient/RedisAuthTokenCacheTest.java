package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.RedisAuthTokenCache;
import com.akgarg.client.authclient.common.AuthToken;
import com.akgarg.client.authclient.config.RedisConnectionConfigs;
import com.akgarg.client.authclient.config.RedisConnectionPoolConfigs;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RedisAuthTokenCacheTest {

    @Test
    @Order(1)
    void addToken_ForSuccessfulInsertion() {
        final var connectionProperty = new RedisConnectionConfigs("localhost", 6379);
        final var connectionPoolConfig = RedisConnectionPoolConfigs.withDefaults();
        final var tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final var userId = "random-user-id";
        final var token = UUID.randomUUID().toString();

        final var expiration = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis();
        final var authToken = new AuthToken(userId, token, expiration);
        final var addResult = tokenCache.addToken(userId, authToken);

        assertTrue(addResult);
    }

    @Test
    @Order(2)
    void getToken_ForSuccessfulFetch() {
        final var connectionProperty = new RedisConnectionConfigs("localhost", 6379);
        final var connectionPoolConfig = RedisConnectionPoolConfigs.withDefaults();
        final var tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final var userId = "random-user-id";
        final var authTokenOptional = tokenCache.getToken(userId);

        assertTrue(authTokenOptional.isPresent());
    }

    @Test
    @Order(3)
    void removeToken_Success() {
        final var connectionProperty = new RedisConnectionConfigs("localhost", 6379);
        final var connectionPoolConfig = RedisConnectionPoolConfigs.withDefaults();
        final var tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final var userId = "random-user-id";
        final var removeTokenResponse = tokenCache.removeToken(userId);

        assertTrue(removeTokenResponse);
    }

    @Test
    @Order(4)
    void getToken_ForUnsuccessfulFetch() {
        final var connectionProperty = new RedisConnectionConfigs("localhost", 6379);
        final var connectionPoolConfig = RedisConnectionPoolConfigs.withDefaults();
        final var tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final var userId = "random-user-id";
        final var authTokenOptional = tokenCache.getToken(userId);

        assertFalse(authTokenOptional.isPresent());
    }

}
