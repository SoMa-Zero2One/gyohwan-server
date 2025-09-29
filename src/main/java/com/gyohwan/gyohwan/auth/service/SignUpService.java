package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.common.domain.LoginType;
import com.gyohwan.gyohwan.common.domain.Social;
import com.gyohwan.gyohwan.common.domain.SocialType;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.SocialRepository;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SignUpService {

    private final UserRepository userRepository;
    private final SocialRepository socialRepository;

    public User createNewKakaoUser(String kakaoUserId) {
        UUID uuid = UUID.randomUUID();
        UUID uuid2 = UUID.randomUUID();
        User user = new User(uuid.toString(), uuid2.toString(), LoginType.SOCIAL);
        Social newSocial = new Social(user, SocialType.KAKAO, kakaoUserId);
        socialRepository.save(newSocial);
        userRepository.save(user);
        return user;
    }
}
