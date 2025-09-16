package com.gyohwan.gyohwan.repository.dto;

import com.gyohwan.gyohwan.domain.Slot;
import lombok.Getter;

@Getter
public class SlotWithApplicantCountDto {

    private Slot slot;
    private Long applicantCount;

    // JPQL의 SELECT NEW ... 구문에서 사용할 생성자
    public SlotWithApplicantCountDto(Slot slot, Long applicantCount) {
        this.slot = slot;
        this.applicantCount = applicantCount;
    }
}