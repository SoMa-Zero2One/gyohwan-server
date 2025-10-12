package com.gyohwan.gyohwan.auth.client;


import com.gyohwan.gyohwan.auth.client.dto.KakaoUserInfoDto;
import com.gyohwan.gyohwan.auth.client.dto.KakaoUserTokenDto;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/*
 * 카카오 인증을 위한 OAuth2 클라이언트
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-code
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
 * https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
 * */
@Component
@RequiredArgsConstructor
@Slf4j
public class KakaoOAuthClient {

    private final RestTemplate restTemplate;
    private final KakaoOAuthClientProperties kakaoOAuthClientProperties;
    private final String KAKAO_TOKEN_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    public KakaoUserInfoDto getUserInfo(String kakaoAccessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(kakaoAccessToken);

        ResponseEntity<KakaoUserInfoDto> response = restTemplate.exchange(
                KAKAO_USER_INFO_URL,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                KakaoUserInfoDto.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return response.getBody();
        }
        return response.getBody();
    }

    public String getAccessTokenFromKakao(String code) {
        try {

            ResponseEntity<KakaoUserTokenDto> response = restTemplate.exchange(
                    buildTokenUri(code),
                    HttpMethod.POST,
                    null,
                    KakaoUserTokenDto.class
            );
            return response.getBody().getAccessToken();
        } catch (Exception e) {
            if (e.getMessage().contains("KOE303")) {
                throw new CustomException(ErrorCode.KAKAO_REDIRECT_URI_MISMATCH);
            }
            if (e.getMessage().contains("KOE320")) {
                throw new CustomException(ErrorCode.INVALID_OR_EXPIRED_KAKAO_AUTH_CODE);
            }
            throw new CustomException(ErrorCode.INVALID_OR_EXPIRED_KAKAO_AUTH_CODE);
        }
    }

    private String buildTokenUri(String code) {
        return UriComponentsBuilder.fromHttpUrl(KAKAO_TOKEN_URL)
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", kakaoOAuthClientProperties.clientId())
                .queryParam("redirect_uri", kakaoOAuthClientProperties.redirectUrl())
                .queryParam("code", code)
                .toUriString();
    }
}
