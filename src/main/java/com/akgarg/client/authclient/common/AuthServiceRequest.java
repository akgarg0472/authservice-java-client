package com.akgarg.client.authclient.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Akhilesh Garg
 * @since 09/09/23
 */
public record AuthServiceRequest(@JsonProperty("user_id") String userId, @JsonProperty("auth_token") String token) {
}
