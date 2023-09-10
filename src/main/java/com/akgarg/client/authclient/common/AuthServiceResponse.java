package com.akgarg.client.authclient.common;

/**
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthServiceResponse(String userId, String token, long expiration, boolean success) {
}
