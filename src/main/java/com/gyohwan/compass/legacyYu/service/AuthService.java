package com.gyohwan.compass.legacyYu.service;

import com.gyohwan.compass.domain.User;
import com.gyohwan.compass.legacyYu.dto.LoginResponse;
import com.gyohwan.compass.legacyYu.dto.UUIDLoginRequest;
import com.gyohwan.compass.legacyYu.security.JwtTokenProvider;
import com.gyohwan.compass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional(readOnly = true)
    public LoginResponse loginWithUuid(UUIDLoginRequest loginRequest) {
        User user = userRepository.findByUuid(loginRequest.getUuid())
                .orElseThrow(() -> new IllegalArgumentException("해당 UUID를 가진 사용자가 없습니다."));

        String accessToken = jwtTokenProvider.createToken(user.getUuid());

        return new LoginResponse(
                accessToken,
                "bearer",
                user.getId(),
                user.getNickname()
        );
    }
}