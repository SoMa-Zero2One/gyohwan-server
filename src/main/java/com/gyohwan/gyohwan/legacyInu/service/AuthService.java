package com.gyohwan.gyohwan.legacyInu.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.legacyInu.dto.LoginResponse;
import com.gyohwan.gyohwan.legacyInu.dto.UUIDLoginRequest;
import com.gyohwan.gyohwan.legacyYu.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("inuAuthService")
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
