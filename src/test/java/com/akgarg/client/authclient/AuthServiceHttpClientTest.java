package com.akgarg.client.authclient;

import com.akgarg.client.authclient.common.ApiVersion;
import com.akgarg.client.authclient.common.AuthServiceEndpoint;
import com.akgarg.client.authclient.common.AuthServiceRequest;
import com.akgarg.client.authclient.common.AuthServiceResponse;
import com.akgarg.client.authclient.http.AuthServiceHttpClient;
import com.akgarg.client.authclient.http.DefaultAuthServiceHttpClient;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link AuthServiceHttpClient}
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
class AuthServiceHttpClientTest {

    @Test
    void queryAuthService_ShouldReturn_EmptyResponse() {
        final var authServiceRequest = new AuthServiceRequest(
                "36f7cfae7e964cc0aa0cf17d006c3e97",
                getAuthToken()
        );

        final var validateTokenEndpoint = "auth/v1/validate-token";
        final var apiVersion = ApiVersion.V1;
        final var httpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint, apiVersion);
        final var authServiceEndpoint = new AuthServiceEndpoint(
                "http",
                "localhost",
                1234
        );

        assertNotNull(httpClient, "httpClient can't be null");
        assertNotNull(authServiceEndpoint, "authServiceEndpoint can't be null");
        assertNotNull(authServiceRequest, "authServiceRequest can't be null");

        final var authServiceResponse = httpClient.queryAuthService(
                authServiceEndpoint,
                authServiceRequest
        );

        assertEquals(Optional.empty(), authServiceResponse);
    }

    @Test
    void queryAuthService_ShouldReturn_SuccessResponse() {
        final AuthServiceRequest authServiceRequest = new AuthServiceRequest(
                "36f7cfae7e964cc0aa0cf17d006c3e97",
                getValidAuthToken()
        );

        final var validateTokenEndpoint = "auth/v1/validate-token";
        final var apiVersion = ApiVersion.V1;
        final var httpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint, apiVersion);
        final var authServiceEndpoint = new AuthServiceEndpoint(
                "http",
                "localhost",
                8085
        );

        final var expectedResult = new AuthServiceResponse(
                authServiceRequest.userId(),
                authServiceRequest.token(),
                1694334647L,
                true
        );

        assertNotNull(httpClient, "httpClient can't be null");
        assertNotNull(authServiceEndpoint, "authServiceEndpoint can't be null");
        assertNotNull(authServiceRequest, "authServiceRequest can't be null");

        final var authServiceResponse = httpClient.queryAuthService(
                authServiceEndpoint,
                authServiceRequest
        );

        assertTrue(authServiceResponse.isPresent());
        assertEquals(expectedResult, authServiceResponse.get());
    }

    private String getValidAuthToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTQzMzQ2NDcsImlhdCI6MTY5NDI3NDY0NywiaXNzIjoiYXV0aC1zZXJ2aWNlIiwic2NvcGVzIjoidXNlciIsInN1YiI6ImpvaG5AZG9lLmNvbSIsInVpZCI6IjM2ZjdjZmFlN2U5NjRjYzBhYTBjZjE3ZDAwNmMzZTk3In0.cYUV7tPoPmT9krMAISjVadJeY_i7OQpdhmmW1LnZhVs";
    }

    @Test
    void queryAuthService_ShouldReturn_FailureResponse() {
        final var authServiceRequest = new AuthServiceRequest(
                "36f7cfae7e964cc0aa0cf17d006c3e97",
                getAuthToken()
        );

        final var validateTokenEndpoint = "auth/v1/validate-token";
        final var apiVersion = ApiVersion.V1;
        final var httpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint, apiVersion);
        final var authServiceEndpoint = new AuthServiceEndpoint(
                "http",
                "localhost",
                8085
        );

        final var expectedResult = new AuthServiceResponse(
                authServiceRequest.userId(),
                authServiceRequest.token(),
                -1L,
                false
        );

        assertNotNull(httpClient, "httpClient can't be null");
        assertNotNull(authServiceEndpoint, "authServiceEndpoint can't be null");
        assertNotNull(authServiceRequest, "authServiceRequest can't be null");

        final var authServiceResponse = httpClient.queryAuthService(
                authServiceEndpoint,
                authServiceRequest
        );

        assertTrue(authServiceResponse.isPresent());
        assertEquals(expectedResult, authServiceResponse.get());
    }

    private String getAuthToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTQzMzQ2NDcsImlhdCI6MTY5NDI3NDY0NywiaXNzIjoiYXV0aC1zZXJ2aWNlIiwic2NvcGVzIjoidXNlciIsInN1YiI6ImpvaG5AZG9lLmNvbSIsInVpZCI6IjM2ZjdjZmFlN2U5NjRjYzBhYTBjZjE3ZDAwNmMzZTk3In0.cYUV7tPoPmT9krMAISjVadJeY_i7OQpdhmmW1LnZhV";
    }

}
