package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;

import java.util.Optional;

/**
 * Object responsible for handling token cache operations
 *
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public sealed interface AuthTokenCache permits InMemoryAuthTokenCache, RedisAuthTokenCache {

    /**
     * Method to fetch the {@link AuthToken} from cache based on the token string value
     *
     * @param userId userId to fetch token for
     * @return Optional of {@link AuthToken}
     */
    Optional<AuthToken> getToken(String userId);

    /**
     * Method to add new token into cache. It overrides existing token if present in cache
     *
     * @param userId userId associated with token
     * @param token  token to add
     */
    boolean addToken(String userId, AuthToken token);

    /**
     * Method to remove token cache
     *
     * @param userId userId associated with token
     */
    boolean removeToken(String userId);

}
