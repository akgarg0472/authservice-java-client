package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.cache.AuthTokenCacheStrategy;
import com.akgarg.client.authclient.cache.InMemoryAuthTokenCache;
import com.akgarg.client.authclient.cache.RedisAuthTokenCache;
import com.akgarg.client.authclient.config.RedisConnectionConfigs;
import com.akgarg.client.authclient.config.RedisConnectionPoolConfigs;
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
    private RedisConnectionConfigs redisConnectionConfigs;
    private AuthTokenCacheStrategy cacheStrategy;
    private RedisConnectionPoolConfigs connectionPoolConfig;

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

    public AuthClientBuilder redisConnectionProperties(final RedisConnectionConfigs redisConnectionConfigs) {
        Objects.requireNonNull(redisConnectionConfigs, "redis connection config can't be null");
        this.redisConnectionConfigs = redisConnectionConfigs;
        return this;
    }

    public AuthClientBuilder redisConnectionPoolConfig(final RedisConnectionPoolConfigs redisConnectionPoolConfigs) {
        Objects.requireNonNull(redisConnectionPoolConfigs, "redis connection pool config is null");
        this.connectionPoolConfig = redisConnectionPoolConfigs;
        return this;
    }

    public AuthClient build() {
        if (AuthTokenCacheStrategy.REDIS.equals(this.cacheStrategy)) {
            this.authTokenCache = new RedisAuthTokenCache(this.redisConnectionConfigs, this.connectionPoolConfig);
        }
        return new DefaultAuthClient(this.authTokenCache, this.authServiceHttpClient);
    }

}
