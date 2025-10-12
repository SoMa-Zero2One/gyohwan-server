package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.client.GoogleOAuthClient;
import com.gyohwan.gyohwan.auth.client.dto.GoogleUserInfoDto;
import com.gyohwan.gyohwan.auth.dto.TokenResponse;
import com.gyohwan.gyohwan.common.domain.Social;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.SocialRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GoogleOAuthService {

    private final GoogleOAuthClient googleOAuthClient;
    private final SocialRepository socialRepository;
    private final LoginService loginService;
    private final SignupService signupService;

    @Transactional
    public TokenResponse processOAuth(String code) {
        String kakaoAccessToken = googleOAuthClient.getAccessTokenFromGoogle(code);
        GoogleUserInfoDto googleUserInfo = googleOAuthClient.getUserInfo(kakaoAccessToken);

        Optional<Social> social = socialRepository.findByExternalId(googleUserInfo.sub());

        User user = null;
        if (social.isPresent()) {
            user = social.get().getUser();
        } else {
            user = signupService.createNewGoogleUser(googleUserInfo.sub());
        }

        return loginService.login(user);
    }
}
