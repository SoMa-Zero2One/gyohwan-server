package com.gyohwan.gyohwan.legacyYu.service;

import com.gyohwan.gyohwan.compare.repository.ChoiceRepository;
import com.gyohwan.gyohwan.compare.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final SlotRepository slotRepository;
    private final ChoiceRepository choiceRepository;

//    @Transactional(readOnly = true)
//    public List<PartnerUniversityInfo> getUniversities() {
//        List<SlotWithApplicantCountDto> results = slotRepository.findBySeasonWithApplicantCounts(1L);
//
//        return results.stream()
//                .map(result -> PartnerUniversityInfo.builder()
//                        .id(result.getSlot().getId()) // 슬롯 ID로 변경
//                        .name(result.getSlot().getOutgoingUniv().getNameKo())
//                        .country(result.getSlot().getOutgoingUniv().getCountry())
//                        .slot(result.getSlot().getSlotCount())
//                        .applicantCount(null)
//                        .build())
//                .collect(Collectors.toList());
//    }

//    @Transactional(readOnly = true)
//    public List<PartnerUniversityInfo> getUniversitiesWithApplicantCount() {
//        // 시즌 ID = 1인 슬롯들만 조회
//        List<SlotWithApplicantCountDto> results = slotRepository.findBySeasonWithApplicantCounts(1L);
//
//        // Controller가 사용할 최종 DTO 리스트로 변환
//        return results.stream()
//                .map(result -> PartnerUniversityInfo.builder()
//                        .id(result.getSlot().getId()) // 슬롯 ID로 변경
//                        .name(result.getSlot().getOutgoingUniv().getNameKo())
//                        .country(result.getSlot().getOutgoingUniv().getCountry())
//                        .slot(result.getSlot().getSlotCount())
//                        .applicantCount(result.getApplicantCount().intValue())
//                        .build())
//                .collect(Collectors.toList());
//    }

//    @Transactional(readOnly = true)
//    public UniversityDetailResponse getUniversityDetails(Long slotId) {
//        // 시즌 ID = 1이면서 해당 슬롯 ID를 가진 슬롯 조회
//        Slot slot = slotRepository.findByIdAndSeasonWithOutgoingUniv(slotId, 1L)
//                .orElseThrow(() -> new IllegalArgumentException("해당 슬롯을 찾을 수 없습니다."));
//
//        List<Choice> applicantsChoices = choiceRepository.findBySlotWithApplicationAndUserOrderByChoiceAsc(slot);
//
//        AtomicInteger rank = new AtomicInteger(1);
//        List<ApplicantDetail> applicantDetails = applicantsChoices.stream()
//                .map(choice -> {
//                    Application application = choice.getApplication();
//                    User user = application.getUser();
//                    Double gradeInfo = user.getGpas().getFirst().getScore();
//
//                    Language language = user.getLanguages().getFirst();
//                    String langInfo = language.getTestType() + " " + language.getScore();
//                    if (language.getGrade() != null && language.getGrade() != "") {
//                        langInfo = language.getTestType() + " " + language.getGrade() + " " + language.getScore();
//                    }
//
//                    return ApplicantDetail.builder()
//                            .id(application.getUser().getId())
//                            .rank(rank.getAndIncrement())
//                            .choice(choice.getChoice())
//                            .nickname(application.getNickname())
//                            .grade(gradeInfo)
//                            .lang(langInfo)
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return UniversityDetailResponse.builder()
//                .name(slot.getOutgoingUniv().getNameKo())
//                .country(slot.getOutgoingUniv().getCountry())
//                .slot(slot.getSlotCount())
//                .totalApplicants(applicantDetails.size())
//                .applicants(applicantDetails)
//                .build();
//    }

}
