package com.akgarg.client.authclient.http;

import com.akgarg.client.authclient.common.ApiVersion;
import com.akgarg.client.authclient.common.AuthServiceEndpoint;
import com.akgarg.client.authclient.common.AuthServiceRequest;
import com.akgarg.client.authclient.common.AuthServiceResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Default implementation of {@link AuthServiceHttpClient}.
 * <p>
 * This class handles the interaction with the authentication service for token validation.
 * It builds HTTP requests, sends them to the configured authentication service, and parses the responses.
 * </p>
 * <p>
 * If a request fails due to an exception, an empty {@link Optional} is returned.
 * If the service responses successfully, the response is parsed and returned as an {@link AuthServiceResponse}.
 * </p>
 *
 * <h3>Features:</h3>
 * <ul>
 *     <li>Asynchronous HTTP calls using {@link HttpClient}.</li>
 *     <li>Configurable API version and endpoint.</li>
 *     <li>Customizable object mapping for response parsing.</li>
 * </ul>
 *
 * @author Akhilesh
 * @since 09/09/23
 */
public final class DefaultAuthServiceHttpClient implements AuthServiceHttpClient {

    private static final Logger log = LoggerFactory.getLogger(DefaultAuthServiceHttpClient.class);

    /**
     * Default token validation endpoint pattern.
     */
    private static final String VALIDATE_TOKEN_ENDPOINT = "api/%s/auth/validate-token";

    private final String validateTokenEndpoint;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    /**
     * Constructs a {@code DefaultAuthServiceHttpClient} instance with the specified parameters.
     *
     * @param validateTokenEndpoint custom token validation endpoint; if {@code null} or blank, a default endpoint is used
     * @param apiVersion            the API version to use in the endpoint
     * @throws NullPointerException if {@code apiVersion} is {@code null}
     */
    public DefaultAuthServiceHttpClient(final String validateTokenEndpoint, final ApiVersion apiVersion) {
        this.validateTokenEndpoint = getValidateTokenEndpoint(apiVersion, validateTokenEndpoint);
        this.objectMapper = createObjectMapperInstance();
        this.httpClient = HttpClient.newHttpClient();
    }

    /**
     * Resolves the validate token endpoint. If a custom endpoint is not provided,
     * the default endpoint is constructed using the specified API version.
     *
     * @param apiVersion            the API version to use in the endpoint
     * @param validateTokenEndpoint custom endpoint value
     * @return resolved endpoint string
     */
    private String getValidateTokenEndpoint(final ApiVersion apiVersion, final String validateTokenEndpoint) {
        if (validateTokenEndpoint == null || validateTokenEndpoint.isBlank()) {
            return VALIDATE_TOKEN_ENDPOINT.formatted(apiVersion.getVersion());
        } else {
            return validateTokenEndpoint;
        }
    }

    /**
     * Creates and configures an {@link ObjectMapper} instance.
     *
     * @return a new {@link ObjectMapper} instance
     */
    private ObjectMapper createObjectMapperInstance() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Sends a POST request to the specified authentication service endpoint with the given request payload.
     * Logs the response details and parses the result into an {@link AuthServiceResponse} if successful.
     * </p>
     *
     * @param endpoint the target authentication service endpoint
     * @param request  the request payload containing token validation details
     * @return an {@link Optional} containing the parsed {@link AuthServiceResponse}, or an empty {@link Optional} if an error occurs
     */
    @Override
    @SuppressWarnings("squid:S2142")
    public Optional<AuthServiceResponse> queryAuthService(
            final AuthServiceEndpoint endpoint,
            final AuthServiceRequest request
    ) {
        try {
            final var httpRequest = createHttpRequest(endpoint, request);
            final var response = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).get();

            if (log.isDebugEnabled()) {
                log.debug("Auth service response code: {}", response.statusCode());
                log.debug("Auth service response: {}", response.body());
            }

            if (response.statusCode() != 200) {
                return Optional.of(new AuthServiceResponse(request.userId(), request.token(), -1, false));
            }

            return Optional.ofNullable(objectMapper.readValue(response.body(), AuthServiceResponse.class));
        } catch (Exception e) {
            log.error("Error '{}' querying auth service on endpoint: {}", e.getMessage(), endpoint);
            return Optional.empty();
        }
    }

    /**
     * Creates an HTTP request for token validation using the provided endpoint and request payload.
     *
     * @param endpoint the authentication service endpoint
     * @param request  the payload for token validation
     * @return a constructed {@link HttpRequest} object
     * @throws JsonProcessingException if the request payload cannot be serialized to JSON
     */
    private HttpRequest createHttpRequest(
            final AuthServiceEndpoint endpoint,
            final AuthServiceRequest request
    ) throws JsonProcessingException {
        final var requestBody = createRequestBody(request);

        return HttpRequest
                .newBuilder()
                .uri(getAuthServiceEndpointURI(endpoint))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .header("Content-Type", "application/json")
                .build();
    }

    /**
     * Builds the URI for the specified authentication service endpoint.
     *
     * @param authServiceEndpoint the endpoint details
     * @return a constructed {@link URI} object
     */
    private URI getAuthServiceEndpointURI(final AuthServiceEndpoint authServiceEndpoint) {
        return URI.create(
                authServiceEndpoint.scheme() + "://" + authServiceEndpoint.host() + ":" + authServiceEndpoint.port() + "/" + this.validateTokenEndpoint
        );
    }

    /**
     * Serializes the given {@link AuthServiceRequest} to a JSON string.
     *
     * @param request the request payload
     * @return a JSON string representation of the request
     * @throws JsonProcessingException if serialization fails
     */
    private String createRequestBody(final AuthServiceRequest request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }

}
