package com.akgarg.client.authclient.common;

import java.io.Serial;
import java.io.Serializable;

/**
 * Represents an authentication token, including the user ID, token value, and expiration time.
 * This class implements {@link Serializable} to allow token serialization.
 *
 * @param userId     the ID of the user associated with the token
 * @param token      the authentication token string
 * @param expiration the expiration time of the token (in milliseconds)
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthToken(String userId, String token, long expiration) implements Serializable {

    @Serial
    private static final long serialVersionUID = -2978486375643768745L;

}
