package com.akgarg.client.authclient.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Akhilesh Garg
 * @since 11/09/23
 */
@ConfigurationProperties(prefix = "auth.client")
public class AuthClientProperties {

    /**
     * Defines the host for redis to connect
     */
    private String redisHost;

    /**
     * Defines the port for redis to connect
     */
    private int redisPort;

    /**
     * Defines the maximum total connections in redis connection pool
     */
    private int redisConnectionPoolMaxTotal = RedisConnectionPoolConfigs.DEFAULT_MAX_TOTAL;

    /**
     * Defines the maximum idle connections in redis connection pool
     */
    private int redisConnectionPoolMaxIdle = RedisConnectionPoolConfigs.DEFAULT_MAX_IDLE;

    /**
     * Defines the minimum idle connections in redis connection pool
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

    public boolean validateRedisConnectionProperties() {
        return this.redisHost != null &&
                !this.redisHost.isBlank() &&
                this.redisPort > 0;
    }

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
