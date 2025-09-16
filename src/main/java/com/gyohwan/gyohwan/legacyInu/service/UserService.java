package com.gyohwan.gyohwan.legacyInu.service;

import com.gyohwan.gyohwan.domain.*;
import com.gyohwan.gyohwan.legacyInu.dto.*;
import com.gyohwan.gyohwan.legacyYu.util.NicknameGenerator;
import com.gyohwan.gyohwan.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("inuUserService")
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
    private final ChoiceRepository choiceRepository;
    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;
    private final DomesticUnivRepository domesticUnivRepository;
    private final GpaRepository gpaRepository;
    private final EmailTriggerService emailTriggerService;

    public UserService(
            UserRepository userRepository,
            ApplicationRepository applicationRepository,
            ChoiceRepository choiceRepository,
            SeasonRepository seasonRepository,
            SlotRepository slotRepository,
            DomesticUnivRepository domesticUnivRepository,
            GpaRepository gpaRepository,
            @Qualifier("inuEmailTriggerService") EmailTriggerService emailTriggerService) {
        this.userRepository = userRepository;
        this.applicationRepository = applicationRepository;
        this.choiceRepository = choiceRepository;
        this.seasonRepository = seasonRepository;
        this.slotRepository = slotRepository;
        this.domesticUnivRepository = domesticUnivRepository;
        this.gpaRepository = gpaRepository;
        this.emailTriggerService = emailTriggerService;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(Long userId) {
        User user = userRepository.findByIdWithApplications(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

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

        Gpa gpa = user.getGpas().getFirst();

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .modifyCount(500)
                .nickname(user.getNickname())
                .grade(gpa.getScore())
                // 인천대는 어학 정보를 표시하지 않음
                .applications(applicationDetails)
                .build();
    }

    @Transactional(readOnly = true)
    public PublicUserResponse getPublicUserInfo(Long userId) {
        User user = userRepository.findByIdWithApplications(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        Gpa gpa = user.getGpas().getFirst();
        Double gpaReturn = null;
        if (gpa != null) {
            gpaReturn = gpa.getScore();
        }

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
                // 인천대는 어학 정보를 표시하지 않음
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

            // 새로운 Choice들 생성 (인천대는 3지망까지만 허용)
            for (UpdateApplicationsRequest.ChoiceUpdateDto choiceUpdate : appUpdate.getChoices()) {
                // 인천대 특화: 3지망을 초과하는 지원은 거부
                if (choiceUpdate.getChoice() > 3) {
                    throw new IllegalArgumentException("인천대는 3지망까지만 지원 가능합니다.");
                }

                Slot slot = slotRepository.findById(choiceUpdate.getSlotId())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 슬롯입니다."));

                Choice choice = createChoice(application, slot, choiceUpdate.getChoice());
                choiceRepository.save(choice);
            }

            // 초이스 정보 로그용 문자열 생성
            String choicesInfo = appUpdate.getChoices().stream()
                    .map(choice -> String.format("순위%d: 슬롯ID %d", choice.getChoice(), choice.getSlotId()))
                    .collect(Collectors.joining(", "));

            log.info("사용자 지원 정보 업데이트 완료 - userId: {}, modifyCount: {}, choiceCount: {}, choices: [{}]",
                    user.getId(),
                    application.getModifyCount(),
                    appUpdate.getChoices().size(),
                    choicesInfo);

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

        // 3. 국내 대학 조회 (ID=2 인천대학교)
        DomesticUniv domesticUniv = domesticUnivRepository.findById(2L)
                .orElseThrow(() -> new IllegalArgumentException("인천대학교 정보를 찾을 수 없습니다."));

        // 4. 임시 닉네임으로 User 생성 후 저장 (ID 생성을 위해)
        String tempNickname = "임시닉네임";
        User user = new User(uuid, request.getEmail(), tempNickname, domesticUniv);
        user = userRepository.save(user);

        // 5. User ID를 기반으로 실제 닉네임 생성 및 업데이트
        String nickname = NicknameGenerator.generateNickname(user.getId());
        user.updateNickname(nickname);
        user = userRepository.save(user);

        // 6. GPA 정보 생성 (4.5 기준으로 가정)
        Gpa gpa = new Gpa(user, request.getGpa(), Gpa.Criteria._4_5);
        gpaRepository.save(gpa);

        // 7. 인천대는 어학 정보를 저장하지 않음 (Language 엔티티 생성 생략)

        // 8. 각 application에 대해 처리
        for (UserRegistrationRequest.ApplicationRegistrationDto appDto : request.getApplications()) {
            // Season 조회
            Season season = seasonRepository.findById(appDto.getSeasonId())
                    .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 시즌입니다: " + appDto.getSeasonId()));

            // Application 생성
            Application application = new Application(user, season, nickname);
            application = applicationRepository.save(application);

            // 각 choice에 대해 처리 (인천대는 3지망까지만)
            for (UserRegistrationRequest.ChoiceRegistrationDto choiceDto : appDto.getChoices()) {
                // 인천대 특화: 3지망을 초과하는 지원은 거부
                if (choiceDto.getChoice() > 3) {
                    throw new IllegalArgumentException("인천대는 3지망까지만 지원 가능합니다.");
                }

                // 슬롯 조회
                Slot slot = slotRepository.findById(choiceDto.getSlotId())
                        .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 슬롯 ID입니다: " + choiceDto.getSlotId()));

                Choice choice = new Choice(application, slot, choiceDto.getChoice());
                choiceRepository.save(choice);
            }
        }

        log.info("인천대 사용자 가입 완료 - userId: {}, email: {}, nickname: {}",
                user.getId(),
                request.getEmail(),
                nickname);

        // 9. 이메일 알람 트리거 (비동기적으로 실행, 실패해도 등록 프로세스에 영향 없음)
        emailTriggerService.triggerNewUserRegistrationEmail(
                request.getEmail(),
                uuid,
                nickname,
                "inu"
        );

        return UserRegistrationResponse.success(user.getId(), uuid, nickname);
    }
}
