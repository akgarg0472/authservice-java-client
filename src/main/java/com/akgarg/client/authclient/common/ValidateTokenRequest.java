package com.akgarg.client.authclient.common;

import java.util.List;

/**
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record ValidateTokenRequest(String userId, String token, List<AuthServiceEndpoint> authServiceEndpoints) {

    public boolean validate() {
        return userId != null && !userId.isBlank() &&
                token != null && !token.isBlank() &&
                authServiceEndpoints != null && !authServiceEndpoints.isEmpty();
    }

}
