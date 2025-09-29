package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.dto.SignInResponse;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class SignInService {

    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public SignInResponse signIn(User user) {
        String accessToken = jwtTokenProvider.createToken(user.getUuid());
        return new SignInResponse(accessToken);
    }

}
