package com.akgarg.client.authclient.redis;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public record RedisConnectionProperty(String host, int port) {

    public static RedisConnectionProperty withDefaults() {
        return new RedisConnectionProperty("localhost", 6379);
    }

}
