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
        SlotDetailResponse response;
        if (userDetails != null) {
            Long userId = Long.parseLong(userDetails.getUsername());
            response = slotService.findSlot(slotId, userId);
        } else {
            response = slotService.publicFindSlot(slotId);
        }
        return ResponseEntity.ok(response);
    }
}

