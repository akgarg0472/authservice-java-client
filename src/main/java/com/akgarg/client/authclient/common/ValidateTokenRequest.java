package com.akgarg.client.authclient.common;

import java.util.List;

/**
 * Represents a request to validate an authentication token.
 * It includes the user ID, token, and a list of authentication service endpoints.
 *
 * <p>The request can be validated using the {@link #validate()} method to check if all fields are properly populated.</p>
 *
 * @param userId               the ID of the user whose token is to be validated
 * @param token                the authentication token to validate
 * @param authServiceEndpoints a list of authentication service endpoints to be used for validation
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record ValidateTokenRequest(String userId, String token, List<AuthServiceEndpoint> authServiceEndpoints) {

    /**
     * Validates the request by ensuring that the user ID, token, and authentication service endpoints are not null or blank.
     *
     * @return true if the request is valid, false otherwise
     */
    public boolean validate() {
        return userId != null && !userId.isBlank() &&
                token != null && !token.isBlank() &&
                authServiceEndpoints != null && !authServiceEndpoints.isEmpty();
    }

    /**
     * Returns a string representation of the ValidateTokenRequest, excluding sensitive token information.
     *
     * @return a string representation of the request
     */
    @Override
    public String toString() {
        return "ValidateTokenRequest{" +
                "userId='" + userId + '\'' +
                ", authServiceEndpoints=" + authServiceEndpoints +
                '}';
    }

}
