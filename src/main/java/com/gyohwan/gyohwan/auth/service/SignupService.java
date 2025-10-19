package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.common.domain.LoginType;
import com.gyohwan.gyohwan.common.domain.Social;
import com.gyohwan.gyohwan.common.domain.SocialType;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.SocialRepository;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.service.NicknameGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SignupService {

    private final UserRepository userRepository;
    private final SocialRepository socialRepository;
    private final NicknameGenerator nicknameGenerator;

    public User createNewKakaoUser(String kakaoUserId) {
        User user = createNewUser(LoginType.SOCIAL);
        Social newSocial = new Social(user, SocialType.KAKAO, kakaoUserId);
        socialRepository.save(newSocial);
        userRepository.save(user);
        return user;
    }

    public User createNewGoogleUser(String googleUserId) {
        User user = createNewUser(LoginType.SOCIAL);
        Social newSocial = new Social(user, SocialType.GOOGLE, googleUserId);
        socialRepository.save(newSocial);
        userRepository.save(user);
        return user;
    }

    public User createNewUser(LoginType loginType) {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        User user = new User(uuid.toString(), nicknameGenerator.generate(), loginType);
        return user;
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }
}
