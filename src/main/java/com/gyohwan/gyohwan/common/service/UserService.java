package com.gyohwan.gyohwan.common.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.dto.*;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.domain.Gpa;
import com.gyohwan.gyohwan.compare.domain.Language;
import com.gyohwan.gyohwan.compare.repository.GpaRepository;
import com.gyohwan.gyohwan.compare.repository.LanguageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final GpaRepository gpaRepository;
    private final LanguageRepository languageRepository;

    @Transactional(readOnly = true)
    public MyUserResponse findUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return MyUserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserGpaResponse findUserGpas(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return UserGpaResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserLanguageResponse findUserLanguages(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        return UserLanguageResponse.from(user);
    }

    @Transactional
    public GpaResponse createGpa(Long userId, CreateGpaRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Gpa.Criteria criteria = convertToCriteria(request.criteria());
        Gpa gpa = new Gpa(user, request.score(), criteria);
        user.getGpas().add(gpa);

        Gpa savedGpa = gpaRepository.save(gpa);
        return GpaResponse.from(savedGpa);
    }

    @Transactional
    public LanguageResponse createLanguage(Long userId, CreateLanguageRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Language.TestType testType = Language.TestType.valueOf(request.testType());
        Language language = new Language(user, testType, request.score(), request.grade());
        user.getLanguages().add(language);

        Language savedLanguage = languageRepository.save(language);
        return LanguageResponse.from(savedLanguage);
    }

    private Gpa.Criteria convertToCriteria(Double criteria) {
        if (Math.abs(criteria - 4.5) < 0.01) return Gpa.Criteria._4_5;
        if (Math.abs(criteria - 4.3) < 0.01) return Gpa.Criteria._4_3;
        if (Math.abs(criteria - 4.0) < 0.01) return Gpa.Criteria._4_0;
        throw new IllegalArgumentException("Invalid GPA criteria. Supported values are: 4.0, 4.3, 4.5");
    }
}
