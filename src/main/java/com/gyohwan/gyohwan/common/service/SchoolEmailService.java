package com.gyohwan.gyohwan.common.service;

import com.gyohwan.gyohwan.common.domain.DomesticUniv;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.dto.SchoolVerificationInfo;
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

        // ⭐️ 수정: DTO 객체 생성
        SchoolVerificationInfo verificationInfo = new SchoolVerificationInfo(
                schoolEmail,
                domesticUniv.getId(),
                verificationCode
        );

        // ⭐️ 수정: 문자열 대신 DTO 객체를 Redis에 저장
        // RedisConfig 설정에 따라 이 객체는 JSON으로 직렬화되어 저장됩니다.
        redisTemplate.opsForValue().set(
                SCHOOL_EMAIL_PREFIX + userId,
                verificationInfo, // ⭐️ 객체를 직접 전달
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

        // ⭐️ 수정: Redis에서 DTO 객체로 조회
        // RedisConfig에 <String, Object>로 설정했으므로 Object로 받습니다.
        Object storedObject = redisTemplate.opsForValue().get(SCHOOL_EMAIL_PREFIX + userId);

        if (storedObject == null) {
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_CONFIRM_REQUEST_NOT_FOUND);
        }

        // ⭐️ 수정: DTO 객체로 캐스팅
        // RedisConfig 설정에 따라 Jackson이 이미 VerificationInfo 객체로 역직렬화해줍니다.
        SchoolVerificationInfo storedInfo;
        try {
            storedInfo = (SchoolVerificationInfo) storedObject;
        } catch (ClassCastException e) {
            // 이전에 저장된 String 데이터가 있거나, 다른 타입이 저장된 경우
            log.warn("Redis data for key {} is corrupted. Expected VerificationInfo.", SCHOOL_EMAIL_PREFIX + userId, e);
            // delete(key)를 호출해서 잘못된 데이터를 지워주는 것도 좋습니다.
            redisTemplate.delete(SCHOOL_EMAIL_PREFIX + userId);
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_CONFIRM_DATA_CORRUPTED);
        }


        // ⭐️ 수정: 파싱 대신 DTO의 Getter 사용
        String schoolEmail = storedInfo.getSchoolEmail();
        Long univId = storedInfo.getUnivId();
        String storedCode = storedInfo.getCode();

        // ⭐️ (선택) Null 체크 - 데이터 안정성 강화
        if (schoolEmail == null || univId == null || storedCode == null) {
            log.warn("Redis data for key {} has null fields.", SCHOOL_EMAIL_PREFIX + userId);
            throw new CustomException(ErrorCode.SCHOOL_EMAIL_CONFIRM_DATA_CORRUPTED);
        }

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
        return email.substring(atIndex + 1); // cau.ac.kr 형태로 반환
    }

    private String generateVerificationCode() {
        // 6자리 숫자 코드 생성
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
    }
}
