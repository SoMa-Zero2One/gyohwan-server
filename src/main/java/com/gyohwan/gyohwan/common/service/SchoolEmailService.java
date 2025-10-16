package com.gyohwan.gyohwan.common.service;

import com.gyohwan.gyohwan.common.domain.DomesticUniv;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.DomesticUnivRepository;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchoolEmailService {

    private final UserRepository userRepository;
    private final DomesticUnivRepository domesticUnivRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long VERIFICATION_CODE_EXPIRATION_SECONDS = 300; // 5분
    private static final String SCHOOL_EMAIL_PREFIX = "school:";

    @Transactional
    public String requestSchoolEmailVerification(Long userId, String schoolEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 이미 학교 인증이 완료된 경우 에러
        if (Boolean.TRUE.equals(user.getSchoolVerified())) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_ALREADY_VERIFIED);
        }

        // 이메일 도메인 추출
        String emailDomain = extractEmailDomain(schoolEmail);

        // 해당 도메인의 학교 찾기
        DomesticUniv domesticUniv = domesticUnivRepository.findByEmailDomain(emailDomain)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_EMAIL_DOMAIN_NOT_SUPPORTED));

        // 인증 코드 생성
        String verificationCode = generateVerificationCode();

        // Redis에 저장 (key: school:{userId}, value: {email}:{univId}:{code})
        String redisValue = schoolEmail + ":" + domesticUniv.getId() + ":" + verificationCode;
        redisTemplate.opsForValue().set(
                SCHOOL_EMAIL_PREFIX + userId,
                redisValue,
                VERIFICATION_CODE_EXPIRATION_SECONDS,
                TimeUnit.SECONDS
        );

        // 이메일 발송
        emailService.sendVerificationEmail(schoolEmail, verificationCode);
        log.info("학교 이메일 인증 코드 발송 완료. UserId: {}, Email: {}, Code: {}", userId, schoolEmail, verificationCode);

        return schoolEmail;
    }

    @Transactional
    public String confirmSchoolEmail(Long userId, String code) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Redis에서 인증 정보 조회
        String storedValue = (String) redisTemplate.opsForValue().get(SCHOOL_EMAIL_PREFIX + userId);

        if (storedValue == null) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_CONFIRM_REQUEST_NOT_FOUND);
        }

        // 저장된 정보 파싱 (email:univId:code)
        String[] parts = storedValue.split(":");
        if (parts.length != 3) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_CONFIRM_REQUEST_NOT_FOUND);
        }

        String schoolEmail = parts[0];
        Long univId = Long.parseLong(parts[1]);
        String storedCode = parts[2];

        // 인증 코드 확인
        if (!storedCode.equals(code)) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_CONFIRM_CODE_DIFFERENT);
        }

        // 학교 정보 조회
        DomesticUniv domesticUniv = domesticUnivRepository.findById(univId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCHOOL_EMAIL_DOMAIN_NOT_SUPPORTED));

        // User 업데이트 (학교 인증 완료)
        user.verifySchool(schoolEmail, domesticUniv);
        userRepository.save(user);

        // Redis에서 삭제
        redisTemplate.delete(SCHOOL_EMAIL_PREFIX + userId);

        log.info("학교 이메일 인증 완료. UserId: {}, SchoolEmail: {}, University: {}", userId, schoolEmail, domesticUniv.getName());

        return schoolEmail;
    }

    private String extractEmailDomain(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_DOMAIN_NOT_SUPPORTED);
        }
        return email.substring(atIndex); // @cau.ac.kr 형태로 반환
    }

    private String generateVerificationCode() {
        // 6자리 숫자 코드 생성
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}

