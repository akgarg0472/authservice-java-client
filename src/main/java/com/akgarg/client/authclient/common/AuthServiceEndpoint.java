package com.akgarg.client.authclient.common;

/**
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthServiceEndpoint(String scheme, String host, int port) {
}