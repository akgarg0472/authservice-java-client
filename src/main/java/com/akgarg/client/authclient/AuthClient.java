package com.akgarg.client.authclient;

import com.akgarg.client.authclient.common.ValidateTokenRequest;

public sealed interface AuthClient permits DefaultAuthClient {

    /**
     * Method to validate authentication token
     *
     * @param validateTokenRequest object containing request data
     * @return true if token is successfully validated else false
     */
    boolean validate(ValidateTokenRequest validateTokenRequest);

}
