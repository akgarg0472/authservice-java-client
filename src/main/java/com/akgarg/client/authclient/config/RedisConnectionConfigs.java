package com.akgarg.client.authclient.config;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public record RedisConnectionConfigs(String host, int port) {

    public static RedisConnectionConfigs withDefaults() {
        return new RedisConnectionConfigs("localhost", 6379);
    }

}
