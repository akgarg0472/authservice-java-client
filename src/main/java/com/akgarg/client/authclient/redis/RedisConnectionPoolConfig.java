package com.akgarg.client.authclient.redis;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public record RedisConnectionPoolConfig(int maxTotal, int maxIdle, int minIdle) {

    public static final int DEFAULT_MAX_TOTAL = 128;
    public static final int DEFAULT_MAX_IDLE = 128;
    public static final int DEFAULT_MIN_IDLE = 16;

    public static RedisConnectionPoolConfig withDefaults() {
        return new RedisConnectionPoolConfig(
                DEFAULT_MAX_TOTAL,
                DEFAULT_MAX_IDLE,
                DEFAULT_MIN_IDLE
        );
    }

}
