package com.akgarg.client.authclient.exception;

/**
 * Exception thrown when there is a failure in connecting to Redis.
 * This class extends {@link RuntimeException} and provides additional details
 * about the connection failure.
 *
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public class RedisConnectivityException extends RuntimeException {

    /**
     * Constructs a new RedisConnectivityException with the specified message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public RedisConnectivityException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
