package com.gyohwan.gyohwan.admin.service;

import com.gyohwan.gyohwan.admin.dto.UpdateUnivInfoRequest;
import com.gyohwan.gyohwan.admin.dto.UpdateUnivInfoResponse;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import com.gyohwan.gyohwan.compare.repository.OutgoingUnivRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 관리자 기능을 제공하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final OutgoingUnivRepository outgoingUnivRepository;

    /**
     * OutgoingUniv의 information 필드를 업데이트
     */
    @Transactional
    public UpdateUnivInfoResponse updateOutgoingUnivInformation(Long univId, UpdateUnivInfoRequest request) {
        OutgoingUniv outgoingUniv = outgoingUnivRepository.findById(univId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_NOT_FOUND));

        outgoingUniv.updateInformation(request.getInformation());
        
        log.info("OutgoingUniv information updated. UnivId: {}, NameEn: {}", univId, outgoingUniv.getNameEn());

        return UpdateUnivInfoResponse.from(outgoingUniv);
    }
}

