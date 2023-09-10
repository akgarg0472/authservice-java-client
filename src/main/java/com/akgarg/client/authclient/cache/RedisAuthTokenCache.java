package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;
import com.akgarg.client.authclient.redis.RedisConnectionPoolConfig;
import com.akgarg.client.authclient.redis.RedisConnectionProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.*;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.akgarg.client.authclient.redis.AuthTokenSerializerDeserializer.deserialize;
import static com.akgarg.client.authclient.redis.AuthTokenSerializerDeserializer.serializeToken;

public final class RedisAuthTokenCache implements AuthTokenCache {

    private static final Logger LOGGER = LogManager.getLogger(RedisAuthTokenCache.class);
    private static final byte[] REDIS_HASH_FIELD = "auth_token".getBytes(StandardCharsets.UTF_8);
    private final JedisPool connectionPool;

    public RedisAuthTokenCache(
            final RedisConnectionProperty connectionProperty,
            final RedisConnectionPoolConfig connectionPoolConfig
    ) {
        Objects.requireNonNull(connectionProperty);
        this.connectionPool = initializeConnectionPool(connectionProperty, connectionPoolConfig);
    }

    @Override
    public Optional<AuthToken> getToken(final String userId) {
        LOGGER.trace("'{}' fetching token in cache", userId);

        try (final Jedis jedis = connectionPool.getResource()) {
            final byte[] authToken = jedis.hget(userId.getBytes(StandardCharsets.UTF_8), REDIS_HASH_FIELD);

            if (authToken == null) {
                return Optional.empty();
            }

            return Optional.of(deserialize(authToken));
        } catch (Exception e) {
            LOGGER.error("error getting token for {}: {}", userId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public boolean addToken(final String userId, final AuthToken token) {
        LOGGER.trace("'{}' adding token in cache", userId);

        try (final Jedis jedis = connectionPool.getResource()) {
            final byte[] key = userId.getBytes(StandardCharsets.UTF_8);
            final byte[] tokenBytes = serializeToken(token);

            final long expiration = (token.expiration() - System.currentTimeMillis()) / 1000;
            LOGGER.trace("'{}' expiration time in seconds is: {}", userId, expiration);

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
        LOGGER.trace("'{}' removing token in cache", userId);

        try (final Jedis jedis = connectionPool.getResource()) {
            return jedis.hdel(userId.getBytes(StandardCharsets.UTF_8), REDIS_HASH_FIELD) == 1;
        } catch (Exception e) {
            LOGGER.error("'{}' error deleting token: {}", userId, e.getMessage());
            return false;
        }
    }

    private JedisPool initializeConnectionPool(
            final RedisConnectionProperty connectionProperty,
            final RedisConnectionPoolConfig redisConnectionPoolConfig
    ) {
        final JedisPoolConfig connectionPoolConfigs = getConnectionPoolConfigs(redisConnectionPoolConfig);
        return new JedisPool(connectionPoolConfigs, connectionProperty.host(), connectionProperty.port());
    }

    private JedisPoolConfig getConnectionPoolConfigs(final RedisConnectionPoolConfig redisConnectionPoolConfig) {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();

        if (redisConnectionPoolConfig != null) {
            poolConfig.setMaxTotal(redisConnectionPoolConfig.maxTotal());
            poolConfig.setMaxIdle(redisConnectionPoolConfig.maxIdle());
            poolConfig.setMinIdle(redisConnectionPoolConfig.minIdle());
        } else {
            poolConfig.setMaxTotal(RedisConnectionPoolConfig.DEFAULT_MAX_TOTAL);
            poolConfig.setMaxIdle(RedisConnectionPoolConfig.DEFAULT_MAX_IDLE);
            poolConfig.setMinIdle(RedisConnectionPoolConfig.DEFAULT_MIN_IDLE);
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

}
