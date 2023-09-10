package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.cache.AuthTokenCacheStrategy;
import com.akgarg.client.authclient.cache.InMemoryAuthTokenCache;
import com.akgarg.client.authclient.cache.RedisAuthTokenCache;
import com.akgarg.client.authclient.redis.RedisConnectionPoolConfig;
import com.akgarg.client.authclient.redis.RedisConnectionProperty;
import com.akgarg.client.authclient.http.AuthServiceHttpClient;
import com.akgarg.client.authclient.http.DefaultAuthServiceHttpClient;

import java.util.Objects;

/**
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public final class AuthClientBuilder {

    private final AuthServiceHttpClient authServiceHttpClient;
    private AuthTokenCache authTokenCache;
    private RedisConnectionProperty redisConnectionProperty;
    private AuthTokenCacheStrategy cacheStrategy;
    private RedisConnectionPoolConfig connectionPoolConfig;

    private AuthClientBuilder() {
        this.authTokenCache = new InMemoryAuthTokenCache();
        this.authServiceHttpClient = new DefaultAuthServiceHttpClient();
    }

    public static AuthClientBuilder builder() {
        return new AuthClientBuilder();
    }

    public AuthClientBuilder cacheStrategy(final AuthTokenCacheStrategy cacheStrategy) {
        Objects.requireNonNull(cacheStrategy, "cache strategy can't be null");
        this.cacheStrategy = cacheStrategy;
        return this;
    }

    public AuthClientBuilder redisConnectionProperties(final RedisConnectionProperty redisConnectionProperty) {
        Objects.requireNonNull(redisConnectionProperty, "redis connection config can't be null");
        this.redisConnectionProperty = redisConnectionProperty;
        return this;
    }

    public AuthClientBuilder redisConnectionPoolConfig(final RedisConnectionPoolConfig redisConnectionPoolConfig) {
        Objects.requireNonNull(redisConnectionPoolConfig, "redis connection pool config is null");
        this.connectionPoolConfig = redisConnectionPoolConfig;
        return this;
    }

    public AuthClient build() {
        if (AuthTokenCacheStrategy.REDIS.equals(this.cacheStrategy)) {
            this.authTokenCache = new RedisAuthTokenCache(this.redisConnectionProperty, this.connectionPoolConfig);
        }
        return new DefaultAuthClient(this.authTokenCache, this.authServiceHttpClient);
    }

}
