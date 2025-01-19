package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;
import com.akgarg.client.authclient.config.RedisConnectionConfigs;
import com.akgarg.client.authclient.config.RedisConnectionPoolConfigs;
import com.akgarg.client.authclient.exception.RedisConnectivityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static com.akgarg.client.authclient.cache.AuthTokenSerializerDeserializer.deserialize;
import static com.akgarg.client.authclient.cache.AuthTokenSerializerDeserializer.serializeToken;

/**
 * A Redis-backed cache for storing and managing authentication tokens.
 * This class handles token retrieval, addition, removal, and periodic connection validation.
 */
public final class RedisAuthTokenCache implements AuthTokenCache {

    private static final byte[] REDIS_HASH_FIELD = "auth_token".getBytes(StandardCharsets.UTF_8);
    private static final Logger log = LoggerFactory.getLogger(RedisAuthTokenCache.class);

    private final JedisPool connectionPool;

    /**
     * Constructs a RedisAuthTokenCache instance with the specified connection configurations.
     *
     * @param connectionProperty   Redis connection details
     * @param connectionPoolConfig Redis connection pool configurations
     */
    public RedisAuthTokenCache(
            final RedisConnectionConfigs connectionProperty,
            final RedisConnectionPoolConfigs connectionPoolConfig
    ) {
        Objects.requireNonNull(connectionProperty, "please provide valid redisConnectionProperty");
        this.connectionPool = initializeConnectionPool(connectionProperty, connectionPoolConfig);
        ping();
        registerCleanupShutdownHook();
        log.info("Redis auth token cache initialized");
    }

    @Override
    public Optional<AuthToken> getToken(final String userId) {
        if (log.isTraceEnabled()) {
            log.trace("Fetching token in cache: '{}'", userId);
        }

        try (final Jedis jedis = connectionPool.getResource()) {
            final var authToken = jedis.hget(userId.getBytes(StandardCharsets.UTF_8), REDIS_HASH_FIELD);

            if (authToken == null) {
                return Optional.empty();
            }

            return Optional.of(deserialize(authToken));
        } catch (Exception e) {
            log.error("Error getting token for {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean addToken(final String userId, final AuthToken token) {
        if (log.isTraceEnabled()) {
            log.trace("'{}' adding token in cache", userId);
        }

        try (final Jedis jedis = connectionPool.getResource()) {
            final var key = userId.getBytes(StandardCharsets.UTF_8);
            final var tokenBytes = serializeToken(token);
            final var expiration = (token.expiration() - System.currentTimeMillis()) / 1000;

            if (log.isTraceEnabled()) {
                log.trace("'{}' expiration time in seconds is: {}", userId, expiration);
            }

            final var pipeline = jedis.pipelined();
            pipeline.hset(key, REDIS_HASH_FIELD, tokenBytes);
            pipeline.expire(key, expiration);
            pipeline.sync();

            return true;
        } catch (Exception e) {
            log.error("error adding token to redis: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeToken(final String userId) {
        if (log.isTraceEnabled()) {
            log.trace("'{}' removing token in cache", userId);
        }

        try (final var jedis = connectionPool.getResource()) {
            return jedis.hdel(userId.getBytes(StandardCharsets.UTF_8), REDIS_HASH_FIELD) == 1;
        } catch (Exception e) {
            log.error("'{}' error deleting token: {}", userId, e.getMessage());
            return false;
        }
    }

    /**
     * Initializes the connection pool with the given Redis connection configurations.
     *
     * @param connectionProperty         Redis connection details
     * @param redisConnectionPoolConfigs Redis connection pool configurations
     * @return a configured JedisPool instance
     */
    private JedisPool initializeConnectionPool(
            final RedisConnectionConfigs connectionProperty,
            final RedisConnectionPoolConfigs redisConnectionPoolConfigs
    ) {
        final var connectionPoolConfigs = getConnectionPoolConfigs(redisConnectionPoolConfigs);
        return new JedisPool(connectionPoolConfigs, connectionProperty.host(), connectionProperty.port());
    }

    /**
     * Creates and returns a JedisPoolConfig instance based on the provided pool configurations.
     *
     * @param redisConnectionPoolConfigs Redis connection pool configurations
     * @return a JedisPoolConfig instance
     */
    private JedisPoolConfig getConnectionPoolConfigs(final RedisConnectionPoolConfigs redisConnectionPoolConfigs) {
        final var poolConfig = new JedisPoolConfig();

        if (redisConnectionPoolConfigs != null) {
            poolConfig.setMaxTotal(redisConnectionPoolConfigs.maxTotal());
            poolConfig.setMaxIdle(redisConnectionPoolConfigs.maxIdle());
            poolConfig.setMinIdle(redisConnectionPoolConfigs.minIdle());
        } else {
            poolConfig.setMaxTotal(RedisConnectionPoolConfigs.DEFAULT_MAX_TOTAL);
            poolConfig.setMaxIdle(RedisConnectionPoolConfigs.DEFAULT_MAX_IDLE);
            poolConfig.setMinIdle(RedisConnectionPoolConfigs.DEFAULT_MIN_IDLE);
        }

        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);

        return poolConfig;
    }

    /**
     * Pings the Redis server to check connectivity.
     */
    private void ping() {
        try (final var jedis = connectionPool.getResource()) {
            final var pingResponse = jedis.ping();
            if (log.isDebugEnabled()) {
                log.debug("Redis ping response: {}", pingResponse);
            }
        } catch (Exception e) {
            throw new RedisConnectivityException("PING to redis failed", e);
        }
    }

    /**
     * Method to register shutdown hook to close redis connection pool
     */
    private void registerCleanupShutdownHook() {
        log.debug("Registering shutdown hook for RedisAuthTokenCache");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                log.info("Shutting down RedisAuthTokenCache...");
                connectionPool.close();
                log.info("Completed shut down of RedisAuthTokenCache");
            } catch (Exception e) {
                log.error("Error shutting down RedisAuthTokenCache: {}", e.getMessage());
            }
        }, "redisAuthTokenCacheShutdownHook"));
    }

}
