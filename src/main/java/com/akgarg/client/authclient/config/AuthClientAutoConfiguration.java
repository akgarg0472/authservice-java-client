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
 * Autoconfiguration class for AuthClient
 *
 * @author Akhilesh Garg
 * @since 10/09/23
 */
@AutoConfiguration
@ConditionalOnClass(AuthClient.class)
@ConditionalOnMissingBean(AuthClient.class)
@EnableConfigurationProperties(AuthClientProperties.class)
public class AuthClientAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthClientAutoConfiguration.class);

    @Bean
    public AuthClient authClient(final AuthClientProperties properties) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AutoConfiguring AuthClient with properties: {}", properties);
        }

        if (properties.validateRedisConnectionProperties()) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Configuring AuthClient with Redis cache");
            }

            return AuthClientBuilder
                    .builder()
                    .redisConnectionProperties(new RedisConnectionConfigs(properties.getRedisHost(), properties.getRedisPort()))
                    .redisConnectionPoolConfig(getRedisConnectionPoolConfig(properties))
                    .cacheStrategy(AuthTokenCacheStrategy.REDIS)
                    .build();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Configuring AuthClient with in-memory cache");
        }

        return AuthClientBuilder
                .builder()
                .build();
    }

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
