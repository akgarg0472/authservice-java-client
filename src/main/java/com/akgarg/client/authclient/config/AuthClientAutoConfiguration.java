package com.akgarg.client.authclient.config;

import com.akgarg.client.authclient.AuthClient;
import com.akgarg.client.authclient.AuthClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
public class AuthClientAutoConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthClientAutoConfiguration.class);

    @Bean
    public AuthClient authClient() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("AutoConfiguring AuthClient with default configuration");
        }

        return AuthClientBuilder
                .builder()
                .build();
    }

}
