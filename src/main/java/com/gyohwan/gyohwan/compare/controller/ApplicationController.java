package com.gyohwan.gyohwan.compare.controller;

import com.gyohwan.gyohwan.compare.dto.ApplicationDetailResponse;
import com.gyohwan.gyohwan.compare.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationDetailResponse> getUserApplication(
            @PathVariable Long applicationId
    ) {
        ApplicationDetailResponse response = applicationService.getApplication(applicationId);
        return ResponseEntity.ok(response);
    }
}

