package com.gyohwan.gyohwan.auth.client;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoOAuthClientProperties(
        String redirectUrl,
        String clientId
) {
}
