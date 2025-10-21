package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.domain.*;
import com.gyohwan.gyohwan.compare.dto.ApplicationDetailResponse;
import com.gyohwan.gyohwan.compare.dto.ApplicationRequest;
import com.gyohwan.gyohwan.compare.dto.ApplicationResponse;
import com.gyohwan.gyohwan.compare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;
    private final GpaRepository gpaRepository;
    private final LanguageRepository languageRepository;
    private final UserRepository userRepository;
    private final NicknameGenerator nicknameGenerator;

    @Transactional
    public ApplicationResponse applyToSeason(Long seasonId, ApplicationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEASON_NOT_FOUND));

        if (applicationRepository.existsByUserIdAndSeasonId(user.getId(), seasonId)) {
            throw new CustomException(ErrorCode.ALREADY_APPLIED);
        }

        String nickname = nicknameGenerator.generate();

        Application application = new Application(user, season, nickname, request.getExtraScore(), season.getDefaultModifyCount());
        applicationRepository.save(application);

        for (ApplicationRequest.ChoiceRequest choiceRequest : request.getChoices()) {
            Slot slot = slotRepository.findById(choiceRequest.getSlotId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SLOT_NOT_FOUND));

            Gpa gpa = gpaRepository.findById(choiceRequest.getGpaId())
                    .orElseThrow(() -> new CustomException(ErrorCode.GPA_NOT_FOUND));

            if (!gpa.getUser().getId().equals(user.getId())) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_GPA);
            }

            Language language = languageRepository.findById(choiceRequest.getLanguageId())
                    .orElseThrow(() -> new CustomException(ErrorCode.LANGUAGE_NOT_FOUND));

            if (!language.getUser().getId().equals(user.getId())) {
                throw new CustomException(ErrorCode.UNAUTHORIZED_LANGUAGE);
            }

            Choice choice = new Choice(application, slot, choiceRequest.getChoice(), gpa, language);
            application.addChoice(choice);
        }

        return ApplicationResponse.from(application);
    }

    public ApplicationDetailResponse getApplication(Long applicationId) {
        // 해당 유저의 최신 지원 정보 조회
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        return ApplicationDetailResponse.from(application);
    }
}

