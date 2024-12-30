package com.akgarg.client.authclient.http;

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
 * Implementation class of {@link AuthServiceHttpClient} responsible for handling the calling to auth service and parsing response as per contract.
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public final class DefaultAuthServiceHttpClient implements AuthServiceHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAuthServiceHttpClient.class);

    private static final String VALIDATE_TOKEN_ENDPOINT = "auth/v1/validate-token";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String validateTokenEndpoint;

    public DefaultAuthServiceHttpClient(final String validateTokenEndpoint) {
        this.objectMapper = createObjectMapperInstance();
        this.httpClient = HttpClient.newHttpClient();
        this.validateTokenEndpoint = getValidateTokenEndpoint(validateTokenEndpoint);
    }

    private String getValidateTokenEndpoint(final String validateTokenEndpoint) {
        return validateTokenEndpoint == null || validateTokenEndpoint.isBlank() ? VALIDATE_TOKEN_ENDPOINT : validateTokenEndpoint;
    }

    private ObjectMapper createObjectMapperInstance() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public Optional<AuthServiceResponse> queryAuthService(
            final AuthServiceEndpoint endpoint,
            final AuthServiceRequest request
    ) {
        try {
            final var httpRequest = createHttpRequest(endpoint, request);
            final var response = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString()).get();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Auth service response code: {}", response.statusCode());
                LOGGER.debug("Auth service response: {}", response.body());
            }

            if (response.statusCode() != 200) {
                return Optional.of(new AuthServiceResponse(request.userId(), request.token(), -1, false));
            }

            return Optional.ofNullable(objectMapper.readValue(response.body(), AuthServiceResponse.class));
        } catch (Exception e) {
            LOGGER.error("Error '{}' querying auth service to endpoint: {}", e.getMessage(), endpoint);
            return Optional.empty();
        }
    }

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

    private URI getAuthServiceEndpointURI(final AuthServiceEndpoint authServiceEndpoint) {
        return URI.create(
                authServiceEndpoint.scheme() + "://" + authServiceEndpoint.host() + ":" + authServiceEndpoint.port() + "/" + this.validateTokenEndpoint
        );
    }

    private String createRequestBody(final AuthServiceRequest request) throws JsonProcessingException {
        return objectMapper.writeValueAsString(request);
    }

}
