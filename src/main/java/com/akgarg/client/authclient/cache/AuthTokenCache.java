package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;

import java.util.Optional;

/**
 * Interface for handling token cache operations.
 * Implementations should provide mechanisms for storing, retrieving, and removing authentication tokens.
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public sealed interface AuthTokenCache permits InMemoryAuthTokenCache, RedisAuthTokenCache {

    /**
     * Fetches the {@link AuthToken} from the cache associated with the given user ID.
     *
     * @param userId the user ID to retrieve the token for
     * @return an {@link Optional} containing the {@link AuthToken} if found, otherwise empty
     */
    Optional<AuthToken> getToken(String userId);

    /**
     * Adds a new authentication token to the cache. If a token already exists for the given user ID, it will be replaced.
     *
     * @param userId the user ID associated with the token
     * @param token  the {@link AuthToken} to add
     * @return true if the token was successfully added or replaced, false otherwise
     */
    boolean addToken(String userId, AuthToken token);

    /**
     * Removes the authentication token associated with the given user ID from the cache.
     *
     * @param userId the user ID to remove the token for
     * @return true if the token was removed successfully, false otherwise
     */
    boolean removeToken(String userId);

}
