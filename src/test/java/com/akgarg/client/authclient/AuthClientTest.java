package com.akgarg.client.authclient;

import com.akgarg.client.authclient.cache.AuthTokenCache;
import com.akgarg.client.authclient.cache.InMemoryAuthTokenCache;
import com.akgarg.client.authclient.common.AuthServiceEndpoint;
import com.akgarg.client.authclient.common.ValidateTokenRequest;
import com.akgarg.client.authclient.http.AuthServiceHttpClient;
import com.akgarg.client.authclient.http.DefaultAuthServiceHttpClient;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for {@link AuthClient}
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
class AuthClientTest {

    @Test
    void authClientValidateMethod_ShouldReturnFalse_WithInvalidRequestLogs() {
        final AuthTokenCache authTokenCache = new InMemoryAuthTokenCache();
        final var validateTokenEndpoint = "auth/v1/validate-token";
        final AuthServiceHttpClient authServiceHttpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint);

        assertNotNull(authTokenCache, "authTokenCache is null");
        assertNotNull(authServiceHttpClient, "authServiceHttpClient is null");

        final AuthClient authClient = new DefaultAuthClient(
                authTokenCache,
                authServiceHttpClient
        );

        final ValidateTokenRequest validateTokenRequest = new ValidateTokenRequest(
                null,
                null,
                Collections.emptyList()
        );

        final boolean validateResponse = authClient.validate(validateTokenRequest);

        System.out.println("Validation response: " + validateResponse);
    }

    @Test
    void authClientValidateMethod_ShouldReturnFalse_WithConnectionExceptionErrorLogs() {
        final AuthTokenCache authTokenCache = new InMemoryAuthTokenCache();
        final var validateTokenEndpoint = "auth/v1/validate-token";
        final AuthServiceHttpClient authServiceHttpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint);

        assertNotNull(authTokenCache, "authTokenCache is null");
        assertNotNull(authServiceHttpClient, "authServiceHttpClient is null");

        final boolean validateResponse = isValidateResponse(authTokenCache, authServiceHttpClient);

        assertFalse(validateResponse);
    }

    @Test
    void authClientValidateMethod_ShouldReturnFalse_WithFewConnectionExceptionErrorLogs() {
        final AuthTokenCache authTokenCache = new InMemoryAuthTokenCache();
        final var validateTokenEndpoint = "auth/v1/validate-token";
        final AuthServiceHttpClient authServiceHttpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint);

        assertNotNull(authTokenCache, "authTokenCache is null");
        assertNotNull(authServiceHttpClient, "authServiceHttpClient is null");

        final AuthClient authClient = new DefaultAuthClient(
                authTokenCache,
                authServiceHttpClient
        );

        final ValidateTokenRequest validateTokenRequest = new ValidateTokenRequest(
                "36f7cfae7e964cc0aa0cf17d006c3e98", // this userId is mismatched from userId in token
                getAuthToken(),
                List.of(
                        new AuthServiceEndpoint("http", "localhost", 8081),
                        new AuthServiceEndpoint("http", "localhost", 8082),
                        new AuthServiceEndpoint("http", "localhost", 8085)
                )
        );

        final boolean validateResponse = authClient.validate(validateTokenRequest);

        assertFalse(validateResponse);
    }

    @Test
    void authClientValidateMethod_ShouldReturnTrue_WithFewConnectionExceptionErrorLogs() {
        final AuthTokenCache authTokenCache = new InMemoryAuthTokenCache();
        final var validateTokenEndpoint = "auth/v1/validate-token";
        final AuthServiceHttpClient authServiceHttpClient = new DefaultAuthServiceHttpClient(validateTokenEndpoint);

        assertNotNull(authTokenCache, "authTokenCache is null");
        assertNotNull(authServiceHttpClient, "authServiceHttpClient is null");

        final boolean validateResponse = isValidateResponse(authTokenCache, authServiceHttpClient);

        assertTrue(validateResponse);
    }

    private boolean isValidateResponse(
            final AuthTokenCache authTokenCache,
            final AuthServiceHttpClient authServiceHttpClient
    ) {
        final AuthClient authClient = new DefaultAuthClient(
                authTokenCache,
                authServiceHttpClient
        );

        final ValidateTokenRequest validateTokenRequest = new ValidateTokenRequest(
                "36f7cfae7e964cc0aa0cf17d006c3e97",
                getAuthToken(),
                List.of(
                        new AuthServiceEndpoint("http", "localhost", 1234),
                        new AuthServiceEndpoint("http", "localhost", 5678),
                        new AuthServiceEndpoint("http", "localhost", 9012)
                )
        );

        return authClient.validate(validateTokenRequest);
    }

    private String getAuthToken() {
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJleHAiOjE2OTQzMzQ2NDcsImlhdCI6MTY5NDI3NDY0NywiaXNzIjoiYXV0aC1zZXJ2aWNlIiwic2NvcGVzIjoidXNlciIsInN1YiI6ImpvaG5AZG9lLmNvbSIsInVpZCI6IjM2ZjdjZmFlN2U5NjRjYzBhYTBjZjE3ZDAwNmMzZTk3In0.cYUV7tPoPmT9krMAISjVadJeY_i7OQpdhmmW1LnZhVs";
    }

}
