package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class InMemoryAuthTokenCache implements AuthTokenCache {

    private static final Logger LOGGER = LogManager.getLogger(InMemoryAuthTokenCache.class);

    private final Map<String, AuthToken> cacheMap;

    public InMemoryAuthTokenCache() {
        cacheMap = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<AuthToken> getToken(final String userId) {
        Objects.requireNonNull(userId, "userId can't be null");
        final var authToken = cacheMap.get(userId);
        LOGGER.debug("auth token fetched for '{}' is {}", userId, authToken);
        return Optional.ofNullable(authToken);
    }

    @Override
    public boolean addToken(final String userId, final AuthToken token) {
        Objects.requireNonNull(userId, "userId can't be null");
        Objects.requireNonNull(token, "auth token can't be null");
        LOGGER.debug("adding auth token: {}", token);
        cacheMap.put(token.token(), token);
        return true;
    }

    @Override
    public boolean removeToken(final String userId) {
        Objects.requireNonNull(userId, "userId can't be null");
        LOGGER.debug("removing token for {}", userId);
        final AuthToken removedToken = cacheMap.remove(userId);
        return removedToken != null;
    }

}