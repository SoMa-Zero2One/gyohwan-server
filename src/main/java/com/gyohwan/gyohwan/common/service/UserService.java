package com.gyohwan.gyohwan.common.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.dto.*;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.domain.Gpa;
import com.gyohwan.gyohwan.compare.domain.Language;
import com.gyohwan.gyohwan.compare.repository.GpaRepository;
import com.gyohwan.gyohwan.compare.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GpaRepository gpaRepository;
    private final LanguageRepository languageRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MyUserResponse findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return MyUserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserGpaResponse findUserGpas(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserGpaResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserLanguageResponse findUserLanguages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return UserLanguageResponse.from(user);
    }

    @Transactional
    public GpaResponse createGpa(Long userId, CreateGpaRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Gpa.Criteria criteria = convertToCriteria(request.criteria());
        Gpa gpa = new Gpa(user, request.score(), criteria);
        user.getGpas().add(gpa);

        Gpa savedGpa = gpaRepository.save(gpa);
        return GpaResponse.from(savedGpa);
    }

    @Transactional
    public LanguageResponse createLanguage(Long userId, CreateLanguageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Language.TestType testType;
        try {
            testType = Language.TestType.valueOf(request.testType());
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_LANGUAGE_TEST_TYPE);
        }
        
        Language language = new Language(user, testType, request.score(), request.grade());
        user.getLanguages().add(language);

        Language savedLanguage = languageRepository.save(language);
        return LanguageResponse.from(savedLanguage);
    }

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.PASSWORD_CHANGE_FAILED);
        }

        // 비밀번호 유효성 검사
        if (newPassword.length() < 8) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }

        // 새 비밀번호로 변경
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setEmailPassword(user.getEmail(), hashedNewPassword);
        userRepository.save(user);
    }

    private Gpa.Criteria convertToCriteria(Double criteria) {
        if (Math.abs(criteria - 4.5) < 0.01) return Gpa.Criteria._4_5;
        if (Math.abs(criteria - 4.3) < 0.01) return Gpa.Criteria._4_3;
        if (Math.abs(criteria - 4.0) < 0.01) return Gpa.Criteria._4_0;
        throw new CustomException(ErrorCode.INVALID_GPA_CRITERIA);
    }
}
