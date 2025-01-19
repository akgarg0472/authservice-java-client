package com.akgarg.client.authclient.common;

/**
 * Represents the endpoint details for authentication service.
 * This includes the scheme (e.g., HTTP, HTTPS), host, and port.
 *
 * @param scheme the scheme of the endpoint (e.g., "http", "https")
 * @param host   the host (e.g., "api.example.com")
 * @param port   the port number (e.g., 8080)
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthServiceEndpoint(String scheme, String host, int port) {
}
