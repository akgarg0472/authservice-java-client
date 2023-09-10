package com.akgarg.client.authclient.config;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public record RedisConnectionPoolConfigs(int maxTotal, int maxIdle, int minIdle) {

    public static final int DEFAULT_MAX_TOTAL = 128;
    public static final int DEFAULT_MAX_IDLE = 128;
    public static final int DEFAULT_MIN_IDLE = 16;

    public static RedisConnectionPoolConfigs withDefaults() {
        return new RedisConnectionPoolConfigs(
                DEFAULT_MAX_TOTAL,
                DEFAULT_MAX_IDLE,
                DEFAULT_MIN_IDLE
        );
    }

}
