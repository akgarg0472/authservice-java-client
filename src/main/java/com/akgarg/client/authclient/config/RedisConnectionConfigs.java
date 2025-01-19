package com.akgarg.client.authclient.config;

/**
 * Configuration class for Redis connection properties.
 * <p>
 * This class encapsulates the host and port configuration required to connect to a Redis server.
 * It is implemented as a <strong>Java record</strong>, providing an immutable and compact representation of these properties.
 * </p>
 * <p>
 * A static factory method {@link #withDefaults()} is provided for creating a configuration with default values.
 * </p>
 *
 * @param host the Redis server hostname or IP address
 * @param port the Redis server port number
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public record RedisConnectionConfigs(String host, int port) {

    /**
     * Creates a {@code RedisConnectionConfigs} instance with default values.
     * <p>
     * The default host is set to <strong>"localhost"</strong>, and the default port is set to <strong>6379</strong>.
     * </p>
     *
     * @return a new {@code RedisConnectionConfigs} instance with default host and port values
     */
    public static RedisConnectionConfigs withDefaults() {
        return new RedisConnectionConfigs("localhost", 6379);
    }

}
