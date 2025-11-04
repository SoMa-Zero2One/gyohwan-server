package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.dto.PasswordResetVerificationInfo;
import com.gyohwan.gyohwan.common.domain.LoginType;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.common.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PasswordResetService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final PasswordEncoder passwordEncoder;

    private static final long PASSWORD_RESET_CODE_EXPIRATION_SECONDS = 600; // 10분
    private static final String PASSWORD_RESET_PREFIX = "password_reset:";

    @Transactional
    public String requestPasswordReset(String email) {
        log.info("Password reset requested. Email: {}", email);

        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("Password reset attempt with non-existent email. Email: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        // 소셜 로그인 사용자는 비밀번호 재설정 불가
        if (user.getLoginType() != LoginType.BASIC) {
            log.warn("Social login user attempted password reset. Email: {}, LoginType: {}", email, user.getLoginType());
            throw new CustomException(ErrorCode.SOCIAL_LOGIN_PASSWORD_RESET_NOT_ALLOWED);
        }

        // 인증 코드 생성 (6자리 숫자)
        String verificationCode = generateVerificationCode();

        // Redis에 인증 정보 저장
        PasswordResetVerificationInfo verificationInfo = new PasswordResetVerificationInfo(
                email,
                verificationCode
        );

        String redisKey = PASSWORD_RESET_PREFIX + email;
        redisTemplate.opsForValue().set(
                redisKey,
                verificationInfo,
                PASSWORD_RESET_CODE_EXPIRATION_SECONDS,
                TimeUnit.SECONDS
        );

        log.info("Password reset verification code saved to Redis. Email: {}, RedisKey: {}, Code: {}, TTL: {}s",
                email, redisKey, verificationCode, PASSWORD_RESET_CODE_EXPIRATION_SECONDS);

        // 이메일 발송
        emailService.sendPasswordResetEmail(email, verificationCode);

        log.info("Password reset request completed. Email: {}", email);
        return email;
    }

    /**
     * 비밀번호 재설정 확인 - 코드 검증 및 비밀번호 변경
     */
    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        log.info("Password reset verification requested. Email: {}, Code: {}", email, code);

        // Redis에서 인증 정보 조회
        String redisKey = PASSWORD_RESET_PREFIX + email;
        PasswordResetVerificationInfo storedInfo = (PasswordResetVerificationInfo) redisTemplate.opsForValue().get(redisKey);

        if (storedInfo == null) {
            log.warn("Password reset verification expired or not found. Email: {}, RedisKey: {}", email, redisKey);
            throw new CustomException(ErrorCode.PASSWORD_RESET_CODE_EXPIRED);
        }

        // 인증 코드 검증
        if (!storedInfo.verificationCode().equals(code)) {
            log.warn("Password reset verification code mismatch. Email: {}, Input code: {}, Stored code: {}",
                    email, code, storedInfo.verificationCode());
            throw new CustomException(ErrorCode.PASSWORD_RESET_CODE_INVALID);
        }

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found during password reset. Email: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        // 비밀번호 유효성 검사
        validatePassword(newPassword);

        // 비밀번호 암호화 및 변경
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setEmailPassword(email, hashedPassword);
        userRepository.save(user);

        // Redis에서 인증 정보 삭제
        redisTemplate.delete(redisKey);

        log.info("Password reset completed. Email: {}, UserId: {}", email, user.getId());
    }

    /**
     * 6자리 숫자 인증 코드 생성
     */
    private String generateVerificationCode() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    /**
     * 비밀번호 유효성 검사 (최소 12자)
     */
    private void validatePassword(String password) {
        if (password.length() < 12) {
            log.warn("Password too short. Length: {}", password.length());
            throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
        }
    }
}


