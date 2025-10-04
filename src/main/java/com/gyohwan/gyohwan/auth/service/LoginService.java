package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.dto.TokenResponse;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class LoginService {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenResponse login(User user) {
        String accessToken = jwtTokenProvider.createToken(user.getId());
        return new TokenResponse(accessToken);
    }

}
