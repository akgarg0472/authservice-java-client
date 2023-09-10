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
import redis.clients.jedis.Pipeline;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

import static com.akgarg.client.authclient.cache.AuthTokenSerializerDeserializer.deserialize;
import static com.akgarg.client.authclient.cache.AuthTokenSerializerDeserializer.serializeToken;

public final class RedisAuthTokenCache implements AuthTokenCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisAuthTokenCache.class);
    private static final byte[] REDIS_HASH_FIELD = "auth_token".getBytes(StandardCharsets.UTF_8);
    private final JedisPool connectionPool;

    public RedisAuthTokenCache(
            final RedisConnectionConfigs connectionProperty,
            final RedisConnectionPoolConfigs connectionPoolConfig
    ) {
        Objects.requireNonNull(connectionProperty, "please provide valid redisConnectionProperty");
        this.connectionPool = initializeConnectionPool(connectionProperty, connectionPoolConfig);
        ping();
        registerCleanupShutdownHook();
        LOGGER.trace("Redis auth token cache initialized");
    }

    @Override
    public Optional<AuthToken> getToken(final String userId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Fetching token in cache: '{}'", userId);
        }

        try (final Jedis jedis = connectionPool.getResource()) {
            final byte[] authToken = jedis.hget(userId.getBytes(StandardCharsets.UTF_8), REDIS_HASH_FIELD);

            if (authToken == null) {
                return Optional.empty();
            }

            return Optional.of(deserialize(authToken));
        } catch (Exception e) {
            LOGGER.error("Error getting token for {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean addToken(final String userId, final AuthToken token) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("'{}' adding token in cache", userId);
        }

        try (final Jedis jedis = connectionPool.getResource()) {
            final byte[] key = userId.getBytes(StandardCharsets.UTF_8);
            final byte[] tokenBytes = serializeToken(token);

            final long expiration = (token.expiration() - System.currentTimeMillis()) / 1000;

            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("'{}' expiration time in seconds is: {}", userId, expiration);
            }

            final Pipeline pipeline = jedis.pipelined();
            pipeline.hset(key, REDIS_HASH_FIELD, tokenBytes);
            pipeline.expire(key, expiration);
            pipeline.sync();

            return true;
        } catch (Exception e) {
            LOGGER.error("error adding token to redis: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean removeToken(final String userId) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("'{}' removing token in cache", userId);
        }

        try (final Jedis jedis = connectionPool.getResource()) {
            return jedis.hdel(userId.getBytes(StandardCharsets.UTF_8), REDIS_HASH_FIELD) == 1;
        } catch (Exception e) {
            LOGGER.error("'{}' error deleting token: {}", userId, e.getMessage());
            return false;
        }
    }

    private JedisPool initializeConnectionPool(
            final RedisConnectionConfigs connectionProperty,
            final RedisConnectionPoolConfigs redisConnectionPoolConfigs
    ) {
        final JedisPoolConfig connectionPoolConfigs = getConnectionPoolConfigs(redisConnectionPoolConfigs);
        return new JedisPool(connectionPoolConfigs, connectionProperty.host(), connectionProperty.port());
    }

    private JedisPoolConfig getConnectionPoolConfigs(final RedisConnectionPoolConfigs redisConnectionPoolConfigs) {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();

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
        poolConfig.setMinEvictableIdleTime(Duration.ofSeconds(60));
        poolConfig.setTimeBetweenEvictionRuns(Duration.ofSeconds(30));
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);

        return poolConfig;
    }


    private void ping() {
        try (final Jedis jedis = connectionPool.getResource()) {
            final String pingResponse = jedis.ping();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Redis ping response: {}", pingResponse);
            }
        } catch (Exception e) {
            throw new RedisConnectivityException("PING to redis failed", e);
        }
    }

    /**
     * Method to register shutdown hook to close redis connection pool
     */
    private void registerCleanupShutdownHook() {
        LOGGER.debug("Registering shutdown hook for RedisAuthTokenCache");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                LOGGER.info("Shutting down RedisAuthTokenCache...");
                connectionPool.close();
                LOGGER.info("Completed shut down of RedisAuthTokenCache");
            } catch (Exception e) {
                LOGGER.error("Error shutting down RedisAuthTokenCache: {}", e.getMessage());
            }
        }, "redisAuthTokenCacheShutdownHook"));
    }

}
