package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.dto.TokenResponse;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class LoginService {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse login(User user) {
        String accessToken = jwtTokenProvider.createToken(user.getId());
        log.info("유저 {}가 로그인하여 토큰 발급", user.getId());
        return new TokenResponse(accessToken);
    }

}
