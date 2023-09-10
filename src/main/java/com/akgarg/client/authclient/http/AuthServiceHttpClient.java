package com.akgarg.client.authclient.http;

import com.akgarg.client.authclient.common.AuthServiceEndpoint;
import com.akgarg.client.authclient.common.AuthServiceRequest;
import com.akgarg.client.authclient.common.AuthServiceResponse;

import java.util.Optional;

/**
 * HTTP client to make API call to auth service to validate auth token. It creates request body and pass to HTTP call to check the validity of auth token.
 * <p>If HTTP call fails due to connectivity or parsing error then it returns empty response but if HTTP call is successful then it returns the appropriate response as per response from auth server</p>
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public sealed interface AuthServiceHttpClient permits DefaultAuthServiceHttpClient {

    /**
     * Method to query AuthService. It tries to query the auth service at provide endpoint for given request.
     *
     * @param endpoint endpoint of auth service API to hit
     * @param request  request body for auth service API
     * @return empty optional if query fails due to any issue else return optional with response from auth service
     */
    Optional<AuthServiceResponse> queryAuthService(AuthServiceEndpoint endpoint, AuthServiceRequest request);

}
