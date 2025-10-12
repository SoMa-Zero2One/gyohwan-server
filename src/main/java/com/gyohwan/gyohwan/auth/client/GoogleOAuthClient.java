package com.gyohwan.gyohwan.auth.client;


import com.gyohwan.gyohwan.auth.client.dto.GoogleUserInfoDto;
import com.gyohwan.gyohwan.auth.client.dto.GoogleUserTokenDto;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/*
 * 구글 인증을 위한 OAuth2 클라이언트
 * https://developers.google.com/identity/protocols/oauth2/web-server?hl=ko
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class GoogleOAuthClient {

    private final RestTemplate restTemplate;
    private final KakaoOAuthClientProperties kakaoOAuthClientProperties;
    private final String GOOGLE_TOKEN_URL = "https://oauth2.googleapis.com/token";
    private final String GOOGLE_USER_INFO_URL = "https://www.googleapis.com/oauth2/v3/userinfo";
    private final GoogleOAuthClientProperties googleOAuthClientProperties;

    public GoogleUserInfoDto getUserInfo(String googleAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(googleAccessToken);

        ResponseEntity<GoogleUserInfoDto> response = restTemplate.exchange(
                GOOGLE_USER_INFO_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                GoogleUserInfoDto.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        return response.getBody();
    }

    public String getAccessTokenFromGoogle(String code) {
        try {
            ResponseEntity<GoogleUserTokenDto> response = restTemplate.exchange(
                    buildTokenUri(code),
                    HttpMethod.POST,
                    null,
                    GoogleUserTokenDto.class
            );
            return response.getBody().getAccessToken();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.INVALID_OR_EXPIRED_GOOGLE_AUTH_CODE);
        }
    }

    private String buildTokenUri(String code) {
        log.info(googleOAuthClientProperties.toString());
        String decode = URLDecoder.decode(code, StandardCharsets.UTF_8);
        return UriComponentsBuilder.fromHttpUrl(GOOGLE_TOKEN_URL)
                .queryParam("client_id", googleOAuthClientProperties.clientId())
                .queryParam("client_secret", googleOAuthClientProperties.clientSecret())
                .queryParam("code", decode)
                .queryParam("grant_type", "authorization_code")
                .queryParam("redirect_uri", googleOAuthClientProperties.redirectUrl())
                .toUriString();
    }
}
