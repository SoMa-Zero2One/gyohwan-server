package com.gyohwan.compass.legacyInu.service;

import com.gyohwan.compass.domain.*;
import com.gyohwan.compass.legacyInu.dto.ApplicantDetail;
import com.gyohwan.compass.legacyInu.dto.PartnerUniversityInfo;
import com.gyohwan.compass.legacyInu.dto.UniversityDetailResponse;
import com.gyohwan.compass.repository.ChoiceRepository;
import com.gyohwan.compass.repository.SlotRepository;
import com.gyohwan.compass.repository.dto.SlotWithApplicantCountDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service("inuUniversityService")
@RequiredArgsConstructor
public class UniversityService {

    private final SlotRepository slotRepository;
    private final ChoiceRepository choiceRepository;

    @Transactional(readOnly = true)
    public List<PartnerUniversityInfo> getUniversities() {
        List<SlotWithApplicantCountDto> results = slotRepository.findBySeasonWithApplicantCounts(2L);

        return results.stream()
                .map(result -> PartnerUniversityInfo.builder()
                        .id(result.getSlot().getId()) // 슬롯 ID로 변경
                        .nameKo(result.getSlot().getOutgoingUniv().getNameKo())
                        .nameEn(result.getSlot().getOutgoingUniv().getNameEn())
                        .country(result.getSlot().getOutgoingUniv().getCountry())
                        .slot(result.getSlot().getSlotCount())
                        .totalApplicants(null) // 공개용에서는 지원자 수 미제공
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PartnerUniversityInfo> getUniversitiesWithApplicantCount() {
        // 시즌 ID = 1인 슬롯들만 조회
        List<SlotWithApplicantCountDto> results = slotRepository.findBySeasonWithApplicantCounts(2L);

        // Controller가 사용할 최종 DTO 리스트로 변환
        return results.stream()
                .map(result -> PartnerUniversityInfo.builder()
                        .id(result.getSlot().getId()) // 슬롯 ID로 변경
                        .nameKo(result.getSlot().getOutgoingUniv().getNameKo())
                        .nameEn(result.getSlot().getOutgoingUniv().getNameEn())
                        .country(result.getSlot().getOutgoingUniv().getCountry())
                        .slot(result.getSlot().getSlotCount())
                        .totalApplicants(result.getApplicantCount().intValue())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UniversityDetailResponse getUniversityDetails(Long slotId) {
        // 시즌 ID = 2이면서 해당 슬롯 ID를 가진 슬롯 조회
        Slot slot = slotRepository.findByIdAndSeasonWithOutgoingUniv(slotId, 2L)
                .orElseThrow(() -> new IllegalArgumentException("해당 슬롯을 찾을 수 없습니다."));

        List<Choice> applicantsChoices = choiceRepository.findBySlotWithApplicationAndUserOrderByChoiceAsc(slot);

        AtomicInteger rank = new AtomicInteger(1);
        List<ApplicantDetail> applicantDetails = applicantsChoices.stream()
                .map(choice -> {
                    Application application = choice.getApplication();
                    User user = application.getUser();
                    Double gradeInfo = user.getGpas().getFirst().getScore();

                    // 인천대는 어학 정보를 표시하지 않음
                    return ApplicantDetail.builder()
                            .userId(application.getUser().getId())
                            .nickname(application.getNickname())
                            .choice(choice.getChoice())
                            .grade(gradeInfo)
                            // 인천대는 어학 정보 필드 없음
                            .build();
                })
                .collect(Collectors.toList());

        return UniversityDetailResponse.builder()
                .id(slot.getId())
                .nameKo(slot.getOutgoingUniv().getNameKo())
                .nameEn(slot.getOutgoingUniv().getNameEn())
                .country(slot.getOutgoingUniv().getCountry())
                .slot(slot.getSlotCount())
                .totalApplicants(applicantDetails.size())
                .applicants(applicantDetails)
                .build();
    }
}
