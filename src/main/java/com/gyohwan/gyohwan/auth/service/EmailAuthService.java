package com.gyohwan.gyohwan.auth.service;

import com.gyohwan.gyohwan.auth.dto.VerificationInfo;
import com.gyohwan.gyohwan.common.domain.LoginType;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.DomesticUnivRepository;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.common.service.EmailService;
import com.gyohwan.gyohwan.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailAuthService {

    private final UserRepository userRepository;
    private final SignupService signupService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DomesticUnivRepository domesticUnivRepository;

    private static final long VERIFICATION_CODE_EXPIRATION_SECONDS = 300; // 5분

    @Transactional
    public String requestEmailVerification(String email, String password) {
        if (!signupService.isEmailAvailable(email)) {
            throw new CustomException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        validatePassword(password);

        String verificationCode = generateVerificationCode();
        String hashedPassword = passwordEncoder.encode(password);

        VerificationInfo verificationInfo = new VerificationInfo(verificationCode, hashedPassword);

        redisTemplate.opsForValue().set(
                email,
                verificationInfo,
                VERIFICATION_CODE_EXPIRATION_SECONDS,
                TimeUnit.SECONDS
        );

        emailService.sendVerificationEmail(email, verificationCode);
        log.info("Verification code sent. Email: {}, Code: {}", email, verificationCode);

        return email;
    }

    @Transactional
    public String confirmEmail(String email, String code) {
        VerificationInfo storedInfo = (VerificationInfo) redisTemplate.opsForValue().get(email);

        if (storedInfo == null) {
            throw new CustomException(ErrorCode.EMAIL_CONFIRM_REQUEST_NOT_FOUND);
        }

        if (!storedInfo.code().equals(code)) {
            throw new CustomException(ErrorCode.EMAIL_CONFIRM_CODE_DIFFERENT);
        }

        User user = signupService.createNewUser(LoginType.BASIC);
        user.setEmailPassword(email, storedInfo.hashedPassword());

        // 학교 이메일인지 확인하고 자동 인증 처리
        autoVerifySchoolEmailIfApplicable(user, email);

        userRepository.save(user);

        redisTemplate.delete(email);

        String accessToken = jwtTokenProvider.createToken(user.getId());

        return accessToken;
    }

    public boolean checkEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    private String generateVerificationCode() {
        // 6자리 숫자 코드 생성
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }

    private void validatePassword(String password) {
        if (password.length() < 12) {
            throw new CustomException(ErrorCode.PASSWORD_TOO_SHORT);
        }
    }

    /**
     * 이메일이 학교 이메일인 경우 자동으로 학교 인증 처리
     */
    private void autoVerifySchoolEmailIfApplicable(User user, String email) {
        try {
            String emailDomain = extractEmailDomain(email);
            domesticUnivRepository.findByEmailDomain(emailDomain)
                    .ifPresent(domesticUniv -> {
                        user.verifySchool(email, domesticUniv);
                        log.info("School email auto-verification completed. Email: {}, University: {}", email, domesticUniv.getName());
                    });
        } catch (Exception e) {
            // 학교 이메일이 아니거나 오류가 발생한 경우 무시 (일반 이메일로 처리)
            log.debug("School email auto-verification unavailable. Email: {}", email);
        }
    }

    /**
     * 이메일 도메인 추출 (@포함)
     */
    private String extractEmailDomain(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return email.substring(atIndex + 1); // cau.ac.kr 형태로 반환
    }
}
