package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.common.*;
import com.akgarg.client.authclient.http.AuthServiceHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

final class DefaultAuthClient implements AuthClient {

    private static final Logger LOGGER = LogManager.getLogger(DefaultAuthClient.class);
    private final Random random;

    private final AuthTokenCache authTokenCache;
    private final AuthServiceHttpClient authServiceHttpClient;

    public DefaultAuthClient(final AuthTokenCache authTokenCache, final AuthServiceHttpClient authServiceHttpClient) {
        Objects.requireNonNull(authTokenCache, "authTokenCache is null");
        Objects.requireNonNull(authServiceHttpClient, "authServiceHttpClient is null");

        this.authTokenCache = authTokenCache;
        this.authServiceHttpClient = authServiceHttpClient;
        this.random = new Random();
    }

    @Override
    public boolean validate(final ValidateTokenRequest request) {
        LOGGER.debug("validating request: {}", request);

        if (!request.validate()) {
            LOGGER.error("invalid validate request: {}", request);
            return false;
        }

        final var authToken = authTokenCache.getToken(request.token());

        LOGGER.debug("[{}] auth token from cache: {}", request.token(), authToken);

        if (authToken.isPresent()) {
            if (!authToken.get().userId().equals(request.userId())) {
                return false;
            }

            return checkExpiration(authToken.get());
        }

        return queryToAuthServiceAndReturnResponse(request);
    }

    /**
     * Method to query auth service for token validation & verification, process and returns the appropriate response.
     *
     * @param request token validation request to validate
     * @return true if token is validated successfully or false otherwise
     */
    private boolean queryToAuthServiceAndReturnResponse(final ValidateTokenRequest request) {
        final var authServiceEndpoints = new ArrayList<>(request.authServiceEndpoints());

        final var authServiceRequest = new AuthServiceRequest(request.userId(), request.token());
        Boolean result = null;

        while (result == null && !authServiceEndpoints.isEmpty()) {
            final var authServiceEndpoint = getRandomAuthServiceEndpoint(authServiceEndpoints);
            final var authServiceResponse = authServiceHttpClient.queryAuthService(authServiceEndpoint, authServiceRequest);

            if (authServiceResponse.isEmpty()) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("removing authServiceEndpoint: {}", authServiceEndpoint);
                }
                authServiceEndpoints.remove(authServiceEndpoint);
                continue;
            }

            LOGGER.debug("auth service response for '{}': {}", request.userId(), authServiceResponse.get());
            result = processAuthServiceResponse(request.userId(), authServiceResponse.get());
            LOGGER.debug("auth service query result for '{}' is {}", request.userId(), result);
        }

        return Objects.requireNonNullElse(result, false);
    }

    /**
     * Method to validate the auth service response. It checks for the <strong>success</strong> field of response and matches the <strong>userId</strong> value of response and request.
     * <p>If validations are successful then it updates the cache and return success response.</p>
     *
     * @param userId   userId from request
     * @param response response from auth service
     * @return true if processing is successful or false otherwise
     */
    private Boolean processAuthServiceResponse(final String userId, final AuthServiceResponse response) {
        if (response.success() && response.userId().equals(userId)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("token validation success: {}", response);
            }
            final var authToken = new AuthToken(userId, response.token(), response.expiration());
            authTokenCache.addToken(response.userId(), authToken);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * Method to retrieve a random auth service endpoint from list of auth service endpoints
     *
     * @param endpoints list of endpoints available
     * @return a completely random endpoint
     */
    private AuthServiceEndpoint getRandomAuthServiceEndpoint(final List<AuthServiceEndpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    /**
     * Method to check the token expiration. This method checks the expiration time of token against the current system time (in millis)
     *
     * @param authToken token to check for expiration
     * @return true if token is non-expired else false
     */
    private boolean checkExpiration(final AuthToken authToken) {
        return authToken.expiration() > System.currentTimeMillis();
    }

}
