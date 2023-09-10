package com.akgarg.client.authclient.redis;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public class RedisConnectivityException extends RuntimeException {

    public RedisConnectivityException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
