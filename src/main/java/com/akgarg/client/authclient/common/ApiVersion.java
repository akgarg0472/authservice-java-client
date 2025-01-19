package com.akgarg.client.authclient.common;

import java.util.Objects;

/**
 * Enumeration representing the available API versions.
 * Currently, only version "v1" is supported.
 *
 * @author Akhilesh Garg
 * @since 10/09/23
 */
public enum ApiVersion {

    V1("v1");

    private final String version;

    /**
     * Constructor to initialize the API version.
     *
     * @param version the version string
     */
    ApiVersion(final String version) {
        this.version = Objects.requireNonNull(version);
    }

    /**
     * Retrieves the version string.
     *
     * @return the version string
     */
    public String getVersion() {
        return this.version;
    }

}
