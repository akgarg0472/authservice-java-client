package com.akgarg.client.authclient.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a request object containing user ID and authentication token
 * for authentication service operations.
 *
 * @param userId the ID of the user making the request
 * @param token  the authentication token associated with the user
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthServiceRequest(
        @JsonProperty("user_id") String userId,
        @JsonProperty("auth_token") String token
) {
}
