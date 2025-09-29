package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.client.KakaoOAuthClient;
import com.gyohwan.gyohwan.auth.client.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class KakaoOAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;

    @Transactional
    public String processOAuth(String code) {
        String kakaoAccessToken = kakaoOAuthClient.getAccessTokenFromKakao(code);
        KakaoUserInfoDto kakaoUserInfo = kakaoOAuthClient.getUserInfo(kakaoAccessToken);
        String kakaoId = kakaoUserInfo.id();
        return kakaoId;
    }
}
