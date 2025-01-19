package com.akgarg.client.authclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties class for AuthClient.
 * <p>
 * This class maps configuration properties prefixed with <strong>"auth.client"</strong> into its fields.
 * It supports properties related to Redis connection settings and Redis connection pooling configurations.
 * </p>
 * <p>
 * These properties can be configured in the application's configuration files (e.g., <code>application.yml</code> or <code>application.properties</code>).
 * </p>
 * <br />
 * Example configuration:
 * <pre>
 * auth.client.redis-host=localhost
 * auth.client.redis-port=6379
 * auth.client.redis-connection-pool-max-total=100
 * auth.client.redis-connection-pool-max-idle=10
 * auth.client.redis-connection-pool-min-idle=5
 * </pre>
 *
 * @author Akhilesh
 * @since 11/09/23
 */
@ConfigurationProperties(prefix = "auth.client")
public class AuthClientProperties {

    /**
     * Redis host for the connection.
     * <p>
     * Specifies the hostname or IP address of the Redis server.
     * </p>
     */
    private String redisHost;

    /**
     * Redis port for the connection.
     * <p>
     * Specifies the port number on which the Redis server is running.
     * </p>
     */
    private int redisPort;

    /**
     * Maximum total connections in the Redis connection pool.
     * <p>
     * Specifies the upper limit for the total number of connections maintained in the pool.
     * Defaults to {@link RedisConnectionPoolConfigs#DEFAULT_MAX_TOTAL}.
     * </p>
     */
    private int redisConnectionPoolMaxTotal = RedisConnectionPoolConfigs.DEFAULT_MAX_TOTAL;

    /**
     * Maximum idle connections in the Redis connection pool.
     * <p>
     * Specifies the maximum number of idle connections allowed in the pool.
     * Defaults to {@link RedisConnectionPoolConfigs#DEFAULT_MAX_IDLE}.
     * </p>
     */
    private int redisConnectionPoolMaxIdle = RedisConnectionPoolConfigs.DEFAULT_MAX_IDLE;

    /**
     * Minimum idle connections in the Redis connection pool.
     * <p>
     * Specifies the minimum number of idle connections to maintain in the pool.
     * Defaults to {@link RedisConnectionPoolConfigs#DEFAULT_MIN_IDLE}.
     * </p>
     */
    private int redisConnectionPoolMinIdle = RedisConnectionPoolConfigs.DEFAULT_MIN_IDLE;

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(final String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(final int redisPort) {
        this.redisPort = redisPort;
    }

    public int getRedisConnectionPoolMaxTotal() {
        return redisConnectionPoolMaxTotal;
    }

    public void setRedisConnectionPoolMaxTotal(final int redisConnectionPoolMaxTotal) {
        this.redisConnectionPoolMaxTotal = redisConnectionPoolMaxTotal;
    }

    public int getRedisConnectionPoolMaxIdle() {
        return redisConnectionPoolMaxIdle;
    }

    public void setRedisConnectionPoolMaxIdle(final int redisConnectionPoolMaxIdle) {
        this.redisConnectionPoolMaxIdle = redisConnectionPoolMaxIdle;
    }

    public int getRedisConnectionPoolMinIdle() {
        return redisConnectionPoolMinIdle;
    }

    public void setRedisConnectionPoolMinIdle(final int redisConnectionPoolMinIdle) {
        this.redisConnectionPoolMinIdle = redisConnectionPoolMinIdle;
    }

    /**
     * Validates whether the Redis connection properties are properly set.
     *
     * @return <code>true</code> if the Redis host is not null or blank, and the Redis port is greater than 0;
     * <code>false</code> otherwise.
     */
    public boolean validateRedisConnectionProperties() {
        return this.redisHost != null &&
                !this.redisHost.isBlank() &&
                this.redisPort > 0;
    }

    /**
     * Validates the Redis connection pool configuration.
     *
     * @return <code>true</code> if all connection pool settings are greater than 0;
     * <code>false</code> otherwise.
     */
    public boolean validateRedisConnectionPoolConfig() {
        return this.redisConnectionPoolMaxTotal > 0 &&
                this.redisConnectionPoolMaxIdle > 0 &&
                this.redisConnectionPoolMinIdle > 0;
    }

    @Override
    public String toString() {
        return "AuthClientProperties{" +
                "redisHost='" + redisHost + '\'' +
                ", redisPort=" + redisPort +
                ", redisConnectionPoolMaxTotal=" + redisConnectionPoolMaxTotal +
                ", redisConnectionPoolMaxIdle=" + redisConnectionPoolMaxIdle +
                ", redisConnectionPoolMinIdle=" + redisConnectionPoolMinIdle +
                '}';
    }

}
