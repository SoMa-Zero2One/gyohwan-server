package com.gyohwan.gyohwan.admin.controller;

import com.gyohwan.gyohwan.admin.annotation.AdminOnly;
import com.gyohwan.gyohwan.admin.dto.UpdateUnivInfoRequest;
import com.gyohwan.gyohwan.admin.dto.UpdateUnivInfoResponse;
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
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * OutgoingUniv의 information 필드 업데이트
     * 
     * @param univId 대학 ID
     * @param request information 업데이트 요청
     * @return 업데이트된 대학 정보
     */
    @AdminOnly
    @PutMapping("/outgoing-univs/{univId}/information")
    public ResponseEntity<UpdateUnivInfoResponse> updateOutgoingUnivInformation(
            @PathVariable Long univId,
            @RequestBody UpdateUnivInfoRequest request) {
        
        UpdateUnivInfoResponse response = adminService.updateOutgoingUnivInformation(univId, request);
        return ResponseEntity.ok(response);
    }
}

