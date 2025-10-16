package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.compare.domain.Slot;
import com.gyohwan.gyohwan.compare.dto.SlotDetailResponse;
import com.gyohwan.gyohwan.compare.repository.ApplicationRepository;
import com.gyohwan.gyohwan.compare.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SlotService {

    private final SlotRepository slotRepository;
    private final ApplicationRepository applicationRepository;

    // 인증된 사용자용 - 지원 여부 확인 후 정보 제공
    public SlotDetailResponse findSlot(Long slotId, Long userId) {
        Slot slot = slotRepository.findByIdWithDetails(slotId)
                .orElseThrow(() -> new CustomException(ErrorCode.SLOT_NOT_FOUND));

        // 사용자가 해당 시즌에 지원했는지 확인
        boolean hasApplied = applicationRepository.existsByUserIdAndSeasonId(userId, slot.getSeason().getId());

        return SlotDetailResponse.from(slot, hasApplied);
    }

    // 미인증 사용자용 - 제한된 정보만 제공
    public SlotDetailResponse publicFindSlot(Long slotId) {
        Slot slot = slotRepository.findByIdWithDetails(slotId)
                .orElseThrow(() -> new CustomException(ErrorCode.SLOT_NOT_FOUND));

        return SlotDetailResponse.from(slot, false);
    }
}

