package com.akgarg.client.authclient.config;

import com.akgarg.client.authclient.AuthClient;
import com.akgarg.client.authclient.AuthClientBuilder;
import com.akgarg.client.authclient.cache.AuthTokenCacheStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfiguration class for {@link AuthClient}.
 * <p>
 * This class provides the necessary Spring configuration to automatically configure an {@link AuthClient} bean.
 * It utilizes Spring Boot's conditional configuration annotations to ensure that the bean is only created if:
 * <ul>
 *     <li>The {@link AuthClient} class is present on the classpath.</li>
 *     <li>No other {@link AuthClient} bean is already defined in the Spring context.</li>
 * </ul>
 * <p>
 * This class supports two caching strategies:
 * <ul>
 *     <li><strong>Redis-based caching:</strong> If Redis connection properties are provided and validated, the
 *     {@link AuthClient} is configured to use Redis as the caching backend.</li>
 *     <li><strong>In-memory caching:</strong> If Redis properties are not valid or missing, the
 *     {@link AuthClient} defaults to an in-memory caching strategy.</li>
 * </ul>
 * </p>
 *
 * @author Akhilesh
 * @since 10/09/23
 */
@AutoConfiguration
@ConditionalOnClass(AuthClient.class)
@ConditionalOnMissingBean(AuthClient.class)
@EnableConfigurationProperties(AuthClientProperties.class)
public class AuthClientAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AuthClientAutoConfiguration.class);

    /**
     * Configures an {@link AuthClient} bean based on the provided properties.
     * <p>
     * This method first checks if the Redis connection properties provided in {@link AuthClientProperties} are valid.
     * If valid, it configures the {@link AuthClient} with Redis caching. Otherwise, it falls back to in-memory caching.
     * </p>
     *
     * @param properties the {@link AuthClientProperties} object containing configuration values.
     * @return a fully configured {@link AuthClient} bean.
     */
    @Bean
    public AuthClient authClient(final AuthClientProperties properties) {
        if (log.isDebugEnabled()) {
            log.info("AutoConfiguring AuthClient with properties: {}", properties);
        }

        if (properties.validateRedisConnectionProperties()) {
            if (log.isDebugEnabled()) {
                log.info("Configuring AuthClient with Redis cache");
            }

            return AuthClientBuilder
                    .builder()
                    .redisConnectionProperties(new RedisConnectionConfigs(properties.getRedisHost(), properties.getRedisPort()))
                    .redisConnectionPoolConfig(getRedisConnectionPoolConfig(properties))
                    .cacheStrategy(AuthTokenCacheStrategy.REDIS)
                    .build();
        }

        if (log.isDebugEnabled()) {
            log.info("Configuring AuthClient with in-memory cache");
        }

        return AuthClientBuilder
                .builder()
                .build();
    }

    /**
     * Creates a {@link RedisConnectionPoolConfigs} object based on the provided {@link AuthClientProperties}.
     * <p>
     * If the Redis connection pool configuration properties are valid, this method initializes the
     * {@link RedisConnectionPoolConfigs} object with those values. Otherwise, it falls back to the default
     * configuration values.
     * </p>
     *
     * @param properties the {@link AuthClientProperties} containing the Redis connection pool configuration values.
     * @return a {@link RedisConnectionPoolConfigs} object with the appropriate configuration values.
     */
    private RedisConnectionPoolConfigs getRedisConnectionPoolConfig(final AuthClientProperties properties) {
        if (properties.validateRedisConnectionPoolConfig()) {
            return new RedisConnectionPoolConfigs(
                    properties.getRedisConnectionPoolMaxTotal(),
                    properties.getRedisConnectionPoolMaxIdle(),
                    properties.getRedisConnectionPoolMinIdle()
            );
        } else {
            return RedisConnectionPoolConfigs.withDefaults();
        }
    }

}
