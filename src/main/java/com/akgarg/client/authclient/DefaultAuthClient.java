package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.common.*;
import com.akgarg.client.authclient.http.AuthServiceHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Default implementation of the {@link AuthClient} interface.
 * <p>
 * This class handles the validation of authentication tokens by leveraging a caching mechanism
 * and querying an external authentication service if necessary. It combines the use of in-memory
 * or Redis-based caching and external API calls for a reliable and performant token validation system.
 * </p>
 * <p>
 * Caching is used to minimize redundant external API calls for token validation. If a token is not found
 * or expired in the cache, the class queries an external authentication service using HTTP.
 * </p>
 *
 * @author Akhilesh
 * @since 09/09/23
 */
final class DefaultAuthClient implements AuthClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultAuthClient.class);

    private final AuthServiceHttpClient authServiceHttpClient;
    private final AuthTokenCache authTokenCache;
    private final Random random;

    /**
     * Constructs a new {@code DefaultAuthClient}.
     *
     * @param authTokenCache        the token cache implementation to use for storing tokens.
     *                              Must not be null.
     * @param authServiceHttpClient the HTTP client used for querying the external authentication service.
     *                              Must not be null.
     * @throws NullPointerException if {@code authTokenCache} or {@code authServiceHttpClient} is null.
     */
    DefaultAuthClient(final AuthTokenCache authTokenCache, final AuthServiceHttpClient authServiceHttpClient) {
        this.authServiceHttpClient = Objects.requireNonNull(authServiceHttpClient, "authServiceHttpClient is null");
        this.authTokenCache = Objects.requireNonNull(authTokenCache, "authTokenCache is null");
        this.random = new Random();
    }

    @Override
    public boolean validate(final ValidateTokenRequest request) {
        if (log.isTraceEnabled()) {
            log.trace("validating request: {}", request);
        }

        if (!request.validate()) {
            log.error("invalid validate request: {}", request);
            return false;
        }

        final var authToken = authTokenCache.getToken(request.userId());

        if (log.isDebugEnabled()) {
            log.debug("Auth token fetched from cache for {}: {}", request.userId(), authToken);
        }

        if (authToken.isPresent()) {
            if (!authToken.get().userId().equals(request.userId())) {
                return false;
            }

            return checkExpiration(authToken.get());
        }

        return queryToAuthServiceAndReturnResponse(request);
    }

    /**
     * Queries the external authentication service for token validation if the token is not in the cache.
     *
     * @param request the {@link ValidateTokenRequest} containing the user ID, token, and service endpoints.
     * @return {@code true} if the token is valid as per the external service; {@code false} otherwise.
     */
    private boolean queryToAuthServiceAndReturnResponse(final ValidateTokenRequest request) {
        final var authServiceEndpoints = new ArrayList<>(request.authServiceEndpoints());
        final var authServiceRequest = new AuthServiceRequest(request.userId(), request.token());
        Boolean result = null;

        while (result == null && !authServiceEndpoints.isEmpty()) {
            final var authServiceEndpoint = getRandomAuthServiceEndpoint(authServiceEndpoints);
            final var authServiceResponse = authServiceHttpClient.queryAuthService(authServiceEndpoint, authServiceRequest);

            if (authServiceResponse.isEmpty()) {
                authServiceEndpoints.remove(authServiceEndpoint);
                continue;
            }

            if (log.isDebugEnabled()) {
                log.debug("Auth service query response for '{}' is {}", request, authServiceResponse.get());
            }

            result = processAuthServiceResponse(request.userId(), authServiceResponse.get());

            if (log.isDebugEnabled()) {
                log.debug("Auth service query result for '{}' is {}", request, result);
            }
        }

        return Objects.requireNonNullElse(result, false);
    }

    /**
     * Processes the response from the external authentication service.
     * <p>
     * If the response indicates success and the user ID matches, the token is added to the cache,
     * and {@code true} is returned. Otherwise, returns {@code false}.
     * </p>
     *
     * @param userId   the user ID from the request.
     * @param response the {@link AuthServiceResponse} received from the authentication service.
     * @return {@code true} if the response is valid and the token was cached; {@code false} otherwise.
     */
    private Boolean processAuthServiceResponse(final String userId, final AuthServiceResponse response) {
        if (response.success() && response.userId().equals(userId)) {
            final var authToken = new AuthToken(userId, response.token(), response.expiration());
            authTokenCache.addToken(response.userId(), authToken);
            return Boolean.TRUE;
        }

        return Boolean.FALSE;
    }

    /**
     * Retrieves a random authentication service endpoint from a list of endpoints.
     *
     * @param endpoints the list of available service endpoints.
     * @return a randomly selected {@link AuthServiceEndpoint}.
     */
    private AuthServiceEndpoint getRandomAuthServiceEndpoint(final List<AuthServiceEndpoint> endpoints) {
        return endpoints.get(random.nextInt(endpoints.size()));
    }

    /**
     * Checks whether the given token has expired based on its expiration timestamp.
     *
     * @param authToken the {@link AuthToken} to check.
     * @return {@code true} if the token has not expired; {@code false} otherwise.
     */
    private boolean checkExpiration(final AuthToken authToken) {
        return authToken.expiration() > System.currentTimeMillis();
    }

}
