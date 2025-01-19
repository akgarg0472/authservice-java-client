package com.akgarg.client.authclient.common;

/**
 * Represents the response from the authentication service, containing
 * user ID, authentication token, token expiration time, and the success status of the request.
 *
 * @param userId     the ID of the user associated with the response
 * @param token      the authentication token issued to the user
 * @param expiration the expiration time of the token (in milliseconds)
 * @param success    indicates whether the request was successful
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthServiceResponse(String userId, String token, long expiration, boolean success) {
}
