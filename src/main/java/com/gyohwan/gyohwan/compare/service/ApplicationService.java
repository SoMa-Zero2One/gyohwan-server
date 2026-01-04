package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.domain.*;
import com.gyohwan.gyohwan.compare.dto.ApplicationDetailResponse;
import com.gyohwan.gyohwan.compare.dto.ApplicationModifyRequest;
import com.gyohwan.gyohwan.compare.dto.ApplicationRequest;
import com.gyohwan.gyohwan.compare.dto.ApplicationResponse;
import com.gyohwan.gyohwan.compare.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;
    private final ChoiceRepository choiceRepository;
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

        // 학교 인증 확인
        if (!user.getSchoolVerified() || user.getDomesticUniv() == null) {
            throw new CustomException(ErrorCode.SCHOOL_NOT_VERIFIED);
        }

        // 사용자의 학교와 시즌의 학교가 일치하는지 확인
        if (!user.getDomesticUniv().getId().equals(season.getDomesticUniv().getId())) {
            throw new CustomException(ErrorCode.UNIV_MISMATCH);
        }

        if (applicationRepository.existsByUserIdAndSeasonId(user.getId(), seasonId)) {
            throw new CustomException(ErrorCode.ALREADY_APPLIED);
        }

        String nickname = nicknameGenerator.generate();

        Application application = new Application(user, season, nickname, request.getExtraScore(), season.getDefaultModifyCount());
        applicationRepository.save(application);

        // 시즌 참여인원 업데이트 (실시간 count)
        long count = applicationRepository.countBySeasonId(seasonId);
        season.setParticipantCount((int) count);
        seasonRepository.save(season);

        Gpa gpa = gpaRepository.findById(request.getGpaId())
                .orElseThrow(() -> new CustomException(ErrorCode.GPA_NOT_FOUND));

        if (!gpa.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_GPA);
        }

        Language language = languageRepository.findById(request.getLanguageId())
                .orElseThrow(() -> new CustomException(ErrorCode.LANGUAGE_NOT_FOUND));

        if (!language.getUser().getId().equals(user.getId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_LANGUAGE);
        }

        if (request.getChoices() == null || request.getChoices().isEmpty()) {
            throw new CustomException(ErrorCode.CHOICES_REQUIRED);
        }

        for (ApplicationRequest.ChoiceRequest choiceRequest : request.getChoices()) {
            Slot slot = slotRepository.findById(choiceRequest.getSlotId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SLOT_NOT_FOUND));

            Choice choice = new Choice(application, slot, choiceRequest.getChoice(), gpa, language);
            application.addChoice(choice);
        }

        log.info("User {} applied to season {}. Application id: {}", user.getId(), seasonId, application.getId());

        return ApplicationResponse.from(application);
    }

    public ApplicationDetailResponse getApplication(Long applicationId) {
        // 해당 유저의 최신 지원 정보 조회
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        log.info("Retrieved application information. Application id: {}", applicationId);
        return ApplicationDetailResponse.from(application);
    }

    public ApplicationDetailResponse getMyApplicationForSeason(Long seasonId, Long userId) {
        // 유저 존재 확인
        userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 시즌 존재 확인
        seasonRepository.findById(seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEASON_NOT_FOUND));

        // 해당 유저의 해당 시즌 지원 정보 조회
        Application application = applicationRepository.findByUserIdAndSeasonIdWithDetails(userId, seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        log.info("Retrieved user {}'s application for season {}", userId, seasonId);
        return ApplicationDetailResponse.from(application);
    }

    @Transactional
    public ApplicationResponse updateApplication(Long seasonId, ApplicationModifyRequest request, Long userId) {
        // 유저 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 시즌 확인
        seasonRepository.findById(seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEASON_NOT_FOUND));

        // 기존 지원서 조회 (choices 포함)
        Application application = applicationRepository.findByUserIdAndSeasonIdWithDetails(userId, seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.APPLICATION_NOT_FOUND));

        // 수정 가능 횟수 확인
        if (application.getModifyCount() <= 0) {
            throw new CustomException(ErrorCode.MODIFY_COUNT_EXCEEDED);
        }

        // Choices 확인
        if (request.getChoices() == null || request.getChoices().isEmpty()) {
            throw new CustomException(ErrorCode.CHOICES_REQUIRED);
        }

        // 기존 application의 첫 번째 choice에서 GPA와 Language 정보 가져오기
        if (application.getChoices().isEmpty()) {
            throw new CustomException(ErrorCode.APPLICATION_NOT_FOUND);
        }

        Choice firstChoice = application.getChoices().get(0);

        // 기존 application의 첫 번째 choice에서 GPA와 Language 정보 가져오기
        // 사용자의 GPA 목록에서 일치하는 것 찾기
        Gpa gpa = user.getGpas().stream()
                .filter(g -> g.getScore().equals(firstChoice.getGpaScore())
                        && g.getCriteria().equals(firstChoice.getGpaCriteria()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.GPA_NOT_FOUND));

        // 사용자의 Language 목록에서 일치하는 것 찾기
        Language language = user.getLanguages().stream()
                .filter(l -> l.getTestType().equals(firstChoice.getLanguageTest())
                        && l.getScore().equals(firstChoice.getLanguageScore()))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.LANGUAGE_NOT_FOUND));

        // 기존 choices를 명시적으로 삭제
        List<Choice> oldChoices = choiceRepository.findByApplicationIdOrderByChoiceAsc(application.getId());
        if (!oldChoices.isEmpty()) {
            choiceRepository.deleteAll(oldChoices);
            choiceRepository.flush(); // 즉시 DB에 반영
        }

        // 기존 choices 리스트 clear
        application.clearChoices();

        // 새로운 choices 추가
        for (ApplicationModifyRequest.ChoiceRequest choiceRequest : request.getChoices()) {
            Slot slot = slotRepository.findById(choiceRequest.getSlotId())
                    .orElseThrow(() -> new CustomException(ErrorCode.SLOT_NOT_FOUND));

            Choice choice = new Choice(application, slot, choiceRequest.getChoice(), gpa, language);
            application.addChoice(choice);
        }

        // 수정 횟수 감소
        application.decrementModifyCount();

        applicationRepository.save(application);
        applicationRepository.flush(); // 변경사항 즉시 DB에 반영

        // 시즌 참여인원 업데이트 (실시간 count)
        Season season = application.getSeason();
        long count = applicationRepository.countBySeasonId(seasonId);
        season.setParticipantCount((int) count);
        seasonRepository.save(season);

        log.info("User {} updated application for season {}. Application id: {}", userId, seasonId, application.getId());
        return ApplicationResponse.from(application);
    }
}

