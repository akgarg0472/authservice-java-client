package com.akgarg.client.authclient.common;

import java.io.Serial;
import java.io.Serializable;

public record AuthToken(String userId, String token, long expiration) implements Serializable {

    @Serial
    private static final long serialVersionUID = -2978486375643768745L;

}
