package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.client.KakaoOAuthClient;
import com.gyohwan.gyohwan.auth.client.dto.KakaoUserInfoDto;
import com.gyohwan.gyohwan.auth.dto.SignInResponse;
import com.gyohwan.gyohwan.common.domain.Social;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.SocialRepository;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class KakaoOAuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final SocialRepository socialRepository;
    private final UserRepository userRepository;
    private final SignInService signInService;
    private final SignUpService signUpService;

    @Transactional
    public SignInResponse processOAuth(String code) {
        String kakaoAccessToken = kakaoOAuthClient.getAccessTokenFromKakao(code);
        KakaoUserInfoDto kakaoUserInfo = kakaoOAuthClient.getUserInfo(kakaoAccessToken);

        Optional<Social> social = socialRepository.findByExternalId(kakaoUserInfo.id());

        User user = null;
        if (social.isPresent()) {
            user = social.get().getUser();
        } else {
            user = signUpService.createNewKakaoUser(kakaoUserInfo.id());
        }

        return signInService.signIn(user);
    }
}
