package com.gyohwan.gyohwan.auth.client;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "oauth.google")
public record GoogleOAuthClientProperties(
        String redirectUrl,
        String clientId,
        String clientSecret
) {
}
