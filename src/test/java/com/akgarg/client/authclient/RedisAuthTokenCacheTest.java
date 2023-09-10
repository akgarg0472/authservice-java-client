package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.cache.RedisAuthTokenCache;
import com.akgarg.client.authclient.common.AuthToken;
import com.akgarg.client.authclient.redis.RedisConnectionPoolConfig;
import com.akgarg.client.authclient.redis.RedisConnectionProperty;
import org.junit.jupiter.api.*;

import java.time.Duration;
import java.util.Optional;
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
        final RedisConnectionProperty connectionProperty = new RedisConnectionProperty("localhost", 6379);
        final RedisConnectionPoolConfig connectionPoolConfig = RedisConnectionPoolConfig.withDefaults();
        final AuthTokenCache tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final String userId = "random-user-id";
        final String token = UUID.randomUUID().toString();

        final long expiration = System.currentTimeMillis() + Duration.ofSeconds(10).toMillis();
        final AuthToken authToken = new AuthToken(userId, token, expiration);
        final boolean addResult = tokenCache.addToken(userId, authToken);

        assertTrue(addResult);
    }

    @Test
    @Order(2)
    void getToken_ForSuccessfulFetch() {
        final RedisConnectionProperty connectionProperty = new RedisConnectionProperty("localhost", 6379);
        final RedisConnectionPoolConfig connectionPoolConfig = RedisConnectionPoolConfig.withDefaults();
        final AuthTokenCache tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final String userId = "random-user-id";
        final Optional<AuthToken> authTokenOptional = tokenCache.getToken(userId);

        assertTrue(authTokenOptional.isPresent());
    }

    @Test
    @Order(3)
    void removeToken_Success() {
        final RedisConnectionProperty connectionProperty = new RedisConnectionProperty("localhost", 6379);
        final RedisConnectionPoolConfig connectionPoolConfig = RedisConnectionPoolConfig.withDefaults();
        final AuthTokenCache tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final String userId = "random-user-id";
        final boolean removeTokenResponse = tokenCache.removeToken(userId);

        assertTrue(removeTokenResponse);
    }

    @Test
    @Order(4)
    void getToken_ForUnsuccessfulFetch() {
        final RedisConnectionProperty connectionProperty = new RedisConnectionProperty("localhost", 6379);
        final RedisConnectionPoolConfig connectionPoolConfig = RedisConnectionPoolConfig.withDefaults();
        final AuthTokenCache tokenCache = new RedisAuthTokenCache(connectionProperty, connectionPoolConfig);

        final String userId = "random-user-id";
        final Optional<AuthToken> authTokenOptional = tokenCache.getToken(userId);

        assertFalse(authTokenOptional.isPresent());
    }

}
