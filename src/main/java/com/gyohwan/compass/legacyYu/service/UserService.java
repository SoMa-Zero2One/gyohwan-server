package com.gyohwan.compass.legacyYu.service;

import com.gyohwan.compass.domain.*;
import com.gyohwan.compass.legacyYu.dto.ApplicationDetail;
import com.gyohwan.compass.legacyYu.dto.PublicUserResponse;
import com.gyohwan.compass.legacyYu.dto.UpdateApplicationsRequest;
import com.gyohwan.compass.legacyYu.dto.UserRegistrationRequest;
import com.gyohwan.compass.legacyYu.dto.UserRegistrationResponse;
import com.gyohwan.compass.legacyYu.dto.UserResponse;
import com.gyohwan.compass.legacyYu.util.NicknameGenerator;
import com.gyohwan.compass.repository.ApplicationRepository;
import com.gyohwan.compass.repository.ChoiceRepository;
import com.gyohwan.compass.repository.DomesticUnivRepository;
import com.gyohwan.compass.repository.GpaRepository;
import com.gyohwan.compass.repository.LanguageRepository;
import com.gyohwan.compass.repository.SeasonRepository;
import com.gyohwan.compass.repository.SlotRepository;
import com.gyohwan.compass.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ChoiceRepository choiceRepository;
    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;
    private final DomesticUnivRepository domesticUnivRepository;
    private final GpaRepository gpaRepository;
    private final LanguageRepository languageRepository;
    private final EmailTriggerService emailTriggerService;

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
                                .universityId(choice.getSlot().getId())
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
                                .universityId(choice.getSlot().getId())
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

    @Transactional
    public UserRegistrationResponse registerNewUser(UserRegistrationRequest request) {
        // 요청 유효성 검증
        if (!request.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 요청입니다.");
        }

        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다: " + request.getEmail());
        }

        // 2. UUID4 생성
        String uuid = UUID.randomUUID().toString();

        // 3. 국내 대학 조회 (ID=1 고정)
        DomesticUniv domesticUniv = domesticUnivRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("국내 대학 ID=1을 찾을 수 없습니다."));

        // 4. 임시 닉네임으로 User 생성 후 저장 (ID 생성을 위해)
        String tempNickname = "임시닉네임";
        User user = new User(uuid, request.getEmail(), tempNickname, domesticUniv);
        user = userRepository.save(user);

        // 5. User ID를 기반으로 실제 닉네임 생성 및 업데이트
        String nickname = NicknameGenerator.generateNickname(user.getId());
        user.updateNickname(nickname);
        user = userRepository.save(user);

        // 6. GPA 정보 생성 (4.3 기준으로 가정)
        Gpa gpa = new Gpa(user, request.getGpa(), Gpa.Criteria._4_5);
        gpaRepository.save(gpa);

        // 7. Language 정보 생성 (나중에 수동 분류용으로 임시 저장)
        Language language = new Language(user, request.getLanguage());
        languageRepository.save(language);

        // 8. 각 application에 대해 처리
        for (UserRegistrationRequest.ApplicationRegistrationDto appDto : request.getApplications()) {
            // Season 조회
            Season season = seasonRepository.findById(appDto.getSeasonId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 시즌입니다: " + appDto.getSeasonId()));

            // Application 생성
            Application application = new Application(user, season, nickname);
            application = applicationRepository.save(application);

            // 각 choice에 대해 처리
            for (UserRegistrationRequest.ChoiceRegistrationDto choiceDto : appDto.getChoices()) {
                // 슬롯 조회
                Slot slot = slotRepository.findById(choiceDto.getSlotId())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 슬롯 ID입니다: " + choiceDto.getSlotId()));
                
                Choice choice = new Choice(application, slot, choiceDto.getChoice());
                choiceRepository.save(choice);
            }
        }

        // 9. 이메일 알람 트리거 (비동기적으로 실행, 실패해도 등록 프로세스에 영향 없음)
        emailTriggerService.triggerNewUserRegistrationEmail(
                request.getEmail(), 
                uuid, 
                nickname, 
                "yu"
        );

        return UserRegistrationResponse.success(user.getId(), uuid, nickname);
    }

    // 다른 비즈니스 로직 메서드들...
}