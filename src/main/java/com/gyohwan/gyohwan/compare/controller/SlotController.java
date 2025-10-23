package com.gyohwan.gyohwan.compare.controller;

import com.gyohwan.gyohwan.compare.dto.SlotDetailResponse;
import com.gyohwan.gyohwan.compare.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/slots")
@RestController
public class SlotController {

    private final SlotService slotService;

    @GetMapping("/{slotId}")
    public ResponseEntity<SlotDetailResponse> findSlot(
            @PathVariable Long slotId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 인증된 유저이고 해당 season에 application이 있는 경우에만 전체 정보 제공
        if (userDetails != null) {
            Long userId = Long.parseLong(userDetails.getUsername());
            boolean hasApplication = slotService.hasApplicationForSlot(slotId, userId);
            
            if (hasApplication) {
                return ResponseEntity.ok(slotService.findSlot(slotId, userId));
            }
        }
        
        // 미인증 또는 해당 season에 application이 없는 경우 제한된 정보 제공
        return ResponseEntity.ok(slotService.publicFindSlot(slotId));
    }
}

