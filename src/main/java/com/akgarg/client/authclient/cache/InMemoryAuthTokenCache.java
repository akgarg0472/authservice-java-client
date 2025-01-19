package com.akgarg.client.authclient.cache;

import com.akgarg.client.authclient.common.AuthToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A simple in-memory cache for storing and managing authentication tokens.
 * It supports token retrieval, addition, and removal. Expired tokens are evicted periodically.
 */
public final class InMemoryAuthTokenCache implements AuthTokenCache {

    private static final Logger log = LoggerFactory.getLogger(InMemoryAuthTokenCache.class);
    private static final String USER_ID_NULL_MSG = "UserId should not be null";

    private final Map<String, AuthToken> cacheMap;
    private final ScheduledExecutorService tokenEvictionScheduler;

    /**
     * Constructs an instance of the cache with a scheduled eviction of expired tokens.
     */
    public InMemoryAuthTokenCache() {
        this.cacheMap = new ConcurrentHashMap<>();
        this.tokenEvictionScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            final var thread = new Thread(r);
            thread.setDaemon(true);
            return thread;
        });
        startEvictionThread();
    }

    /**
     * Starts the scheduled eviction thread to remove expired tokens every 5 minutes.
     */
    private void startEvictionThread() {
        tokenEvictionScheduler.scheduleAtFixedRate(this::evictExpiredTokens, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public Optional<AuthToken> getToken(final String userId) {
        final var authToken = cacheMap.get(Objects.requireNonNull(userId, USER_ID_NULL_MSG));

        if (log.isDebugEnabled()) {
            log.debug("Auth token fetched for '{}' is {}", userId, authToken);
        }

        return Optional.ofNullable(authToken);
    }

    @Override
    public boolean addToken(final String userId, final AuthToken token) {
        Objects.requireNonNull(userId, USER_ID_NULL_MSG);
        Objects.requireNonNull(token, "auth token can't be null");

        if (log.isDebugEnabled()) {
            log.debug("Adding auth token: {}", token);
        }

        cacheMap.put(token.token(), token);
        return true;
    }

    @Override
    public boolean removeToken(final String userId) {
        Objects.requireNonNull(userId, USER_ID_NULL_MSG);

        if (log.isDebugEnabled()) {
            log.debug("Removing auth token for '{}'", userId);
        }

        final var removedToken = cacheMap.remove(userId);
        return removedToken != null;
    }

    /**
     * Evicts tokens that have expired based on their expiration time.
     */
    private void evictExpiredTokens() {
        final var currentTimeMillis = System.currentTimeMillis();
        final var iterator = cacheMap.entrySet().iterator();

        while (iterator.hasNext()) {
            final var entry = iterator.next();
            final var token = entry.getValue();

            if (token.expiration() < currentTimeMillis) {
                iterator.remove();
            }
        }
    }

}