package com.akgarg.client.authclient.exception;

/**
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public class RedisConnectivityException extends RuntimeException {

    public RedisConnectivityException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
