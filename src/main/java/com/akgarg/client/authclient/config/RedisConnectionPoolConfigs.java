package com.akgarg.client.authclient.config;

/**
 * Configuration class for Redis connection pool properties.
 * <p>
 * This class encapsulates the configurations for a Redis connection pool,
 * including the maximum total connections, maximum idle connections, and minimum idle connections.
 * It provides a static factory method {@link #withDefaults()} for creating an instance with default values.
 * </p>
 *
 * <ul>
 * <li>{@code maxTotal} - Maximum number of connections allowed in the pool.</li>
 * <li>{@code maxIdle} - Maximum number of idle connections allowed in the pool.</li>
 * <li>{@code minIdle} - Minimum number of idle connections to maintain in the pool.</li>
 * </ul>
 *
 * @param maxTotal the maximum number of connections allowed in the pool
 * @param maxIdle  the maximum number of idle connections in the pool
 * @param minIdle  the minimum number of idle connections to maintain in the pool
 * @see #withDefaults()
 * @since 10/09/23
 */
public record RedisConnectionPoolConfigs(int maxTotal, int maxIdle, int minIdle) {

    /**
     * The default value for the maximum number of connections in the pool.
     */
    public static final int DEFAULT_MAX_TOTAL = 128;

    /**
     * The default value for the maximum number of idle connections in the pool.
     */
    public static final int DEFAULT_MAX_IDLE = 128;

    /**
     * The default value for the minimum number of idle connections in the pool.
     */
    public static final int DEFAULT_MIN_IDLE = 16;

    /**
     * Creates a {@code RedisConnectionPoolConfigs} instance with default values.
     * <p>
     * The default values are:
     * <ul>
     * <li>{@code maxTotal} = {@value #DEFAULT_MAX_TOTAL}</li>
     * <li>{@code maxIdle} = {@value #DEFAULT_MAX_IDLE}</li>
     * <li>{@code minIdle} = {@value #DEFAULT_MIN_IDLE}</li>
     * </ul>
     * </p>
     *
     * @return a new {@code RedisConnectionPoolConfigs} instance with default configurations
     */
    public static RedisConnectionPoolConfigs withDefaults() {
        return new RedisConnectionPoolConfigs(
                DEFAULT_MAX_TOTAL,
                DEFAULT_MAX_IDLE,
                DEFAULT_MIN_IDLE
        );
    }

}
