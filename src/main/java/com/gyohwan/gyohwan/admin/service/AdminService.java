package com.gyohwan.gyohwan.admin.service;

import com.gyohwan.gyohwan.admin.dto.DataValueDto;
import com.gyohwan.gyohwan.admin.dto.DataValueUpdateDto;
import com.gyohwan.gyohwan.admin.dto.UpdateOutgoingUnivRequest;
import com.gyohwan.gyohwan.admin.dto.UpdateOutgoingUnivResponse;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import com.gyohwan.gyohwan.compare.repository.OutgoingUnivRepository;
import com.gyohwan.gyohwan.window.domain.DataField;
import com.gyohwan.gyohwan.window.domain.DataValue;
import com.gyohwan.gyohwan.window.repository.DataFieldRepository;
import com.gyohwan.gyohwan.window.repository.DataValueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 관리자 기능을 제공하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final OutgoingUnivRepository outgoingUnivRepository;
    private final DataFieldRepository dataFieldRepository;
    private final DataValueRepository dataValueRepository;

    /**
     * OutgoingUniv의 information과 DataValue들을 통합 업데이트
     * - information이 null이 아니면 업데이트
     * - dataValues가 비어있지 않으면 각 DataValue 업데이트 또는 생성
     */
    @Transactional
    public UpdateOutgoingUnivResponse updateOutgoingUniv(Long univId, UpdateOutgoingUnivRequest request) {
        // OutgoingUniv 조회
        OutgoingUniv outgoingUniv = outgoingUnivRepository.findById(univId)
                .orElseThrow(() -> new CustomException(ErrorCode.UNIVERSITY_NOT_FOUND));

        // 1. information 업데이트 (요청에 포함된 경우)
        if (request.getInformation() != null) {
            outgoingUniv.updateInformation(request.getInformation());
            log.info("OutgoingUniv information updated. UnivId: {}, NameEn: {}", univId, outgoingUniv.getNameEn());
        }

        // 2. DataValues 업데이트 (요청에 포함된 경우)
        List<DataValueDto> updatedDataValues = new ArrayList<>();
        if (request.getDataValues() != null && !request.getDataValues().isEmpty()) {
            for (DataValueUpdateDto updateDto : request.getDataValues()) {
                DataValue dataValue = updateOrCreateDataValue(outgoingUniv, updateDto);
                updatedDataValues.add(DataValueDto.from(dataValue));
            }
        }

        log.info("OutgoingUniv updated. UnivId: {}, NameEn: {}, UpdatedDataValues: {}", 
                univId, outgoingUniv.getNameEn(), updatedDataValues.size());

        return UpdateOutgoingUnivResponse.of(outgoingUniv, updatedDataValues);
    }

    /**
     * DataValue를 업데이트하거나 존재하지 않으면 생성
     */
    private DataValue updateOrCreateDataValue(OutgoingUniv outgoingUniv, DataValueUpdateDto updateDto) {
        // DataField 조회
        DataField dataField = dataFieldRepository.findById(updateDto.getFieldId())
                .orElseThrow(() -> new CustomException(ErrorCode.DATA_FIELD_NOT_FOUND));

        // DataField의 EntityType이 UNIV인지 확인
        if (dataField.getEntityType() != DataField.EntityType.UNIV) {
            throw new CustomException(ErrorCode.DATA_FIELD_ENTITY_TYPE_MISMATCH);
        }

        // DataValue 조회 또는 생성
        DataValue dataValue = dataValueRepository.findByFieldAndOutgoingUniv(dataField, outgoingUniv)
                .orElseGet(() -> {
                    DataValue newDataValue = new DataValue(dataField, outgoingUniv, updateDto.getValue());
                    return dataValueRepository.save(newDataValue);
                });

        // 기존 DataValue가 있으면 업데이트
        if (dataValue.getId() != null) {
            dataValue.updateValue(updateDto.getValue());
        }

        log.debug("DataValue updated. FieldId: {}, FieldName: {}, UnivId: {}, Value: {}", 
                dataField.getId(), dataField.getFieldName(), outgoingUniv.getId(), updateDto.getValue());

        return dataValue;
    }
}

