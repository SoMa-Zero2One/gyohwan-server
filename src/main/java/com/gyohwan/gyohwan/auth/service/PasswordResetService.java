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
        log.info("비밀번호 재설정 요청. Email: {}", email);

        // 사용자 존재 여부 확인
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 이메일로 비밀번호 재설정 시도. Email: {}", email);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        // 소셜 로그인 사용자는 비밀번호 재설정 불가
        if (user.getLoginType() != LoginType.BASIC) {
            log.warn("소셜 로그인 사용자가 비밀번호 재설정 시도. Email: {}, LoginType: {}", email, user.getLoginType());
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

        log.info("비밀번호 재설정 인증 코드 Redis 저장 완료. Email: {}, RedisKey: {}, Code: {}, 유효시간: {}초",
                email, redisKey, verificationCode, PASSWORD_RESET_CODE_EXPIRATION_SECONDS);

        // 이메일 발송
        emailService.sendPasswordResetEmail(email, verificationCode);

        log.info("비밀번호 재설정 요청 처리 완료. Email: {}", email);
        return email;
    }

    /**
     * 비밀번호 재설정 확인 - 코드 검증 및 비밀번호 변경
     */
    @Transactional
    public void confirmPasswordReset(String email, String code, String newPassword) {
        log.info("비밀번호 재설정 확인 요청. Email: {}, Code: {}", email, code);

        // Redis에서 인증 정보 조회
        String redisKey = PASSWORD_RESET_PREFIX + email;
        PasswordResetVerificationInfo storedInfo = (PasswordResetVerificationInfo) redisTemplate.opsForValue().get(redisKey);

        if (storedInfo == null) {
            log.warn("비밀번호 재설정 인증 정보가 만료되었거나 존재하지 않음. Email: {}, RedisKey: {}", email, redisKey);
            throw new CustomException(ErrorCode.PASSWORD_RESET_CODE_EXPIRED);
        }

        // 인증 코드 검증
        if (!storedInfo.verificationCode().equals(code)) {
            log.warn("비밀번호 재설정 인증 코드 불일치. Email: {}, 입력 코드: {}, 저장된 코드: {}",
                    email, code, storedInfo.verificationCode());
            throw new CustomException(ErrorCode.PASSWORD_RESET_CODE_INVALID);
        }

        // 사용자 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("비밀번호 재설정 중 사용자를 찾을 수 없음. Email: {}", email);
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

        log.info("비밀번호 재설정 완료. Email: {}, UserId: {}", email, user.getId());
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
            log.warn("비밀번호가 너무 짧음. 길이: {}", password.length());
            throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
        }
    }
}


