package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.cache.AuthTokenCacheStrategy;
import com.akgarg.client.authclient.cache.InMemoryAuthTokenCache;
import com.akgarg.client.authclient.cache.RedisAuthTokenCache;
import com.akgarg.client.authclient.common.ApiVersion;
import com.akgarg.client.authclient.config.RedisConnectionConfigs;
import com.akgarg.client.authclient.config.RedisConnectionPoolConfigs;
import com.akgarg.client.authclient.http.DefaultAuthServiceHttpClient;

import java.util.Objects;

/**
 * Builder class for constructing an {@link AuthClient} instance.
 * <p>
 * The {@link AuthClientBuilder} allows setting various configurations such as
 * cache strategy, Redis connection properties, token validation endpoint,
 * and API version for building an {@link AuthClient} with the specified settings.
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public final class AuthClientBuilder {

    private RedisConnectionPoolConfigs connectionPoolConfig;
    private RedisConnectionConfigs redisConnectionConfigs;
    private AuthTokenCacheStrategy cacheStrategy;
    private String validateTokenEndpoint;
    private ApiVersion apiVersion;

    private AuthClientBuilder() {
        this.apiVersion = ApiVersion.V1; // Default API version
    }

    /**
     * Creates a new instance of the {@link AuthClientBuilder}.
     *
     * @return a new instance of {@link AuthClientBuilder}
     */
    public static AuthClientBuilder builder() {
        return new AuthClientBuilder();
    }

    /**
     * Sets the cache strategy to be used for token storage.
     *
     * @param cacheStrategy the cache strategy to use
     * @return the current {@link AuthClientBuilder} instance
     * @throws NullPointerException if the cache strategy is null
     */
    public AuthClientBuilder cacheStrategy(final AuthTokenCacheStrategy cacheStrategy) {
        this.cacheStrategy = Objects.requireNonNull(cacheStrategy, "cache strategy can't be null");
        return this;
    }

    /**
     * Sets the Redis connection properties for connecting to Redis if the cache strategy is Redis.
     *
     * @param redisConnectionConfigs the Redis connection properties
     * @return the current {@link AuthClientBuilder} instance
     * @throws NullPointerException if the Redis connection properties are null
     */
    public AuthClientBuilder redisConnectionProperties(final RedisConnectionConfigs redisConnectionConfigs) {
        this.redisConnectionConfigs = Objects.requireNonNull(redisConnectionConfigs, "redis connection config can't be null");
        return this;
    }

    /**
     * Sets the Redis connection pool configuration for Redis cache strategy.
     *
     * @param redisConnectionPoolConfigs the Redis connection pool properties
     * @return the current {@link AuthClientBuilder} instance
     * @throws NullPointerException if the Redis connection pool configuration is null
     */
    public AuthClientBuilder redisConnectionPoolConfig(final RedisConnectionPoolConfigs redisConnectionPoolConfigs) {
        this.connectionPoolConfig = Objects.requireNonNull(redisConnectionPoolConfigs, "redis connection pool config is null");
        return this;
    }

    /**
     * Sets the endpoint for token validation requests.
     *
     * @param validateTokenEndpoint the URL of the token validation endpoint
     * @return the current {@link AuthClientBuilder} instance
     * @throws NullPointerException if the validation endpoint is null
     */
    public AuthClientBuilder tokenValidationEndpoint(final String validateTokenEndpoint) {
        this.validateTokenEndpoint = Objects.requireNonNull(validateTokenEndpoint, "validate token endpoint is null");
        return this;
    }

    /**
     * Sets the API version to be used by the authentication client.
     *
     * @param apiVersion the API version
     * @return the current {@link AuthClientBuilder} instance
     * @throws NullPointerException if the API version is null
     */
    public AuthClientBuilder apiVersion(final ApiVersion apiVersion) {
        this.apiVersion = Objects.requireNonNull(apiVersion, "api version can't be null");
        return this;
    }

    /**
     * Builds the {@link AuthTokenCache} based on the specified cache strategy.
     *
     * @return an instance of {@link AuthTokenCache}
     */
    private AuthTokenCache buildAuthTokenCache() {
        if (AuthTokenCacheStrategy.REDIS.equals(this.cacheStrategy)) {
            return new RedisAuthTokenCache(this.redisConnectionConfigs, this.connectionPoolConfig);
        } else {
            return new InMemoryAuthTokenCache();
        }
    }

    /**
     * Builds and returns a new {@link AuthClient} instance based on the provided configurations.
     *
     * @return a new {@link AuthClient} instance
     */
    public AuthClient build() {
        final var authServiceHttpClient = new DefaultAuthServiceHttpClient(this.validateTokenEndpoint, apiVersion);
        final var authTokenCache = buildAuthTokenCache();
        return new DefaultAuthClient(authTokenCache, authServiceHttpClient);
    }

}
