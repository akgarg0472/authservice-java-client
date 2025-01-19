package com.akgarg.client.authclient.http;

import com.akgarg.client.authclient.common.AuthServiceEndpoint;
import com.akgarg.client.authclient.common.AuthServiceRequest;
import com.akgarg.client.authclient.common.AuthServiceResponse;

import java.util.Optional;

/**
 * Represents an HTTP client interface for interacting with authentication service.
 * <p>
 * This interface defines the contract for making API calls to authentication service
 * to validate authentication tokens. Implementations handle the creation of request payloads,
 * communication with the authentication server, and parsing of the response.
 * </p>
 * <p>
 * If the HTTP call encounters an issue, such as a connectivity or parsing error,
 * an empty {@link Optional} is returned. If the call is successful, the response from
 * the authentication service is encapsulated in an {@link AuthServiceResponse} object.
 * </p>
 *
 * @author Akhilesh
 * @since 09/09/23
 */
public sealed interface AuthServiceHttpClient permits DefaultAuthServiceHttpClient {

    /**
     * Queries the authentication service at the specified endpoint with the provided request payload.
     * <p>
     * This method sends the authentication request to the service, processes the response,
     * and returns it as an {@link Optional}. If the query fails due to connectivity,
     * server errors, or invalid response formats, an empty {@link Optional} is returned.
     * </p>
     *
     * @param endpoint the {@link AuthServiceEndpoint} representing the target API endpoint
     * @param request  the {@link AuthServiceRequest} containing the payload for token validation
     * @return an {@link Optional} containing the {@link AuthServiceResponse} if the query is successful,
     * or an empty {@link Optional} if the query fails
     */
    Optional<AuthServiceResponse> queryAuthService(AuthServiceEndpoint endpoint, AuthServiceRequest request);

}
