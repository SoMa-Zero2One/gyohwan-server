package com.gyohwan.gyohwan.admin.controller;

import com.gyohwan.gyohwan.admin.annotation.AdminOnly;
import com.gyohwan.gyohwan.admin.dto.UpdateOutgoingUnivRequest;
import com.gyohwan.gyohwan.admin.dto.UpdateOutgoingUnivResponse;
import com.gyohwan.gyohwan.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 관리자 전용 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * OutgoingUniv의 information과 DataValue들을 통합 업데이트
     * - information만 업데이트: { "information": "내용" }
     * - dataValues만 업데이트: { "dataValues": [{ "fieldId": 1, "value": "값" }] }
     * - 둘 다 업데이트: { "information": "내용", "dataValues": [...] }
     *
     * @param univId  대학 ID
     * @param request 업데이트 요청 (information과 dataValues 선택적)
     * @return 업데이트된 대학 정보
     */
    @AdminOnly
    @PatchMapping("/outgoing-univs/{univId}")
    public ResponseEntity<UpdateOutgoingUnivResponse> updateOutgoingUniv(
            @PathVariable Long univId,
            @RequestBody UpdateOutgoingUnivRequest request) {

        UpdateOutgoingUnivResponse response = adminService.updateOutgoingUniv(univId, request);
        return ResponseEntity.ok(response);
    }
}

