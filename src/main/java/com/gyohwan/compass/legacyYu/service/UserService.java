package com.gyohwan.compass.legacyYu.service;

import com.gyohwan.compass.domain.*;
import com.gyohwan.compass.legacyYu.dto.ApplicationDetail;
import com.gyohwan.compass.legacyYu.dto.PublicUserResponse;
import com.gyohwan.compass.legacyYu.dto.UpdateApplicationsRequest;
import com.gyohwan.compass.legacyYu.dto.UserResponse;
import com.gyohwan.compass.repository.ApplicationRepository;
import com.gyohwan.compass.repository.ChoiceRepository;
import com.gyohwan.compass.repository.SeasonRepository;
import com.gyohwan.compass.repository.SlotRepository;
import com.gyohwan.compass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ChoiceRepository choiceRepository;
    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(Long userId) {
        // Repository에서 유저 정보와 지원 정보를 함께 조회 (fetch join 등 사용)
        User user = userRepository.findByIdWithApplications(userId) // (JPQL로 별도 정의 필요)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // Entity를 DTO로 변환하는 로직
        // 예시: 지원 목록(Application) Entity를 ApplicationDetail DTO로 변환
        List<ApplicationDetail> applicationDetails = user.getApplications().stream()
                .flatMap(app -> app.getChoices().stream()
                        .map(choice -> ApplicationDetail.builder()
                                .choice(choice.getChoice())
                                .universityId(choice.getSlot().getOutgoingUniv().getId())
                                .universityName(choice.getSlot().getOutgoingUniv().getNameKo())
                                .country(choice.getSlot().getOutgoingUniv().getCountry())
                                .slot(choice.getSlot().getSlotCount())
                                .totalApplicants(choice.getSlot().getChoices().size())
                                .build()))
                .collect(Collectors.toList());

        // 최종 UserResponse DTO를 만들어서 반환
        Gpa gpa = user.getGpas().getFirst();
        Language language = user.getLanguages().getFirst();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .modifyCount(500)
                .nickname(user.getNickname())
                .grade(gpa.getScore())
                .lang(language.getTestType() + " " + language.getGrade() + " " + language.getScore())
                .applications(applicationDetails)

                .build();
    }

    @Transactional(readOnly = true)
    public PublicUserResponse getPublicUserInfo(Long userId) {
        // Repository에서 유저 정보와 지원 정보를 함께 조회
        User user = userRepository.findByIdWithApplications(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Gpa gpa = user.getGpas().getFirst();
        Double gpaReturn = null;
        if (gpa != null) {
            gpaReturn = gpa.getScore();
        }

        Language language = user.getLanguages().getFirst();
        String languageReturn = "";
        if (language != null) {
            languageReturn = language.getTestType() + " " + language.getGrade() + " " + language.getScore();
        }

        // 지원 정보를 ApplicationDetail로 변환
        List<ApplicationDetail> applicationDetails = user.getApplications().stream()
                .flatMap(app -> app.getChoices().stream()
                        .map(choice -> ApplicationDetail.builder()
                                .choice(choice.getChoice())
                                .universityId(choice.getSlot().getOutgoingUniv().getId())
                                .universityName(choice.getSlot().getOutgoingUniv().getNameKo())
                                .country(choice.getSlot().getOutgoingUniv().getCountry())
                                .slot(choice.getSlot().getSlotCount())
                                .totalApplicants(choice.getSlot().getChoices().size())
                                .build()))
                .collect(Collectors.toList());

        return PublicUserResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .grade(gpaReturn)
                .lang(languageReturn)
                .applications(applicationDetails)
                .build();
    }

    @Transactional
    public void updateUserApplications(User user, List<UpdateApplicationsRequest.ApplicationUpdateDto> applicationUpdates) {
        for (UpdateApplicationsRequest.ApplicationUpdateDto appUpdate : applicationUpdates) {
            // Season 조회
            Season season = seasonRepository.findById(appUpdate.getSeasonId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 시즌입니다."));

            // 기존 Application 조회 또는 생성
            Application application = applicationRepository.findByUserAndSeason(user, season)
                    .orElseGet(() -> {
                        Application newApp = createApplication(user, season);
                        return applicationRepository.save(newApp);
                    });

            // 기존 Choice들 삭제
            List<Choice> existingChoices = choiceRepository.findByApplicationOrderByChoiceAsc(application);
            choiceRepository.deleteAll(existingChoices);

            // 새로운 Choice들 생성
            for (UpdateApplicationsRequest.ChoiceUpdateDto choiceUpdate : appUpdate.getChoices()) {
                Slot slot = slotRepository.findById(choiceUpdate.getSlotId())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 슬롯입니다."));

                Choice choice = createChoice(application, slot, choiceUpdate.getChoice());
                choiceRepository.save(choice);
            }

            // 수정 횟수 증가
            application.incrementModifyCount();
            applicationRepository.save(application);
        }
    }

    private Application createApplication(User user, Season season) {
        return new Application(user, season, user.getNickname());
    }

    private Choice createChoice(Application application, Slot slot, Integer choiceNumber) {
        return new Choice(application, slot, choiceNumber);
    }

    // 다른 비즈니스 로직 메서드들...
}