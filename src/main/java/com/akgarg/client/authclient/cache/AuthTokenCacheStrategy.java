package com.akgarg.client.authclient.cache;

/**
 * Enumeration representing the available strategies for token caching.
 * The two supported strategies are:
 * <ul>
 *     <li>IN_MEMORY - Use an in-memory cache for storing tokens.</li>
 *     <li>REDIS - Use Redis for external token caching.</li>
 * </ul>
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public enum AuthTokenCacheStrategy {
    IN_MEMORY, REDIS
}