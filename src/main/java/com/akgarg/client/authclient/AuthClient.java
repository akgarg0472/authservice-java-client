package com.akgarg.client.authclient;

import com.akgarg.client.authclient.common.ValidateTokenRequest;

/**
 * Interface for authentication client that validates authentication tokens.
 * <p>
 * Implementations of this interface should provide the logic to validate a token
 * based on the provided {@link ValidateTokenRequest}.
 *
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public sealed interface AuthClient permits DefaultAuthClient {

    /**
     * Validates the authentication token using the provided request data.
     *
     * @param validateTokenRequest the object containing the token validation data
     * @return true if the token is successfully validated, false otherwise
     */
    boolean validate(ValidateTokenRequest validateTokenRequest);

}
