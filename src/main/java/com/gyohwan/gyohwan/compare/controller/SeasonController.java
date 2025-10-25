package com.gyohwan.gyohwan.compare.controller;

import com.gyohwan.gyohwan.compare.dto.*;
import com.gyohwan.gyohwan.compare.service.ApplicationService;
import com.gyohwan.gyohwan.compare.service.SeasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1/seasons")
@RestController
public class SeasonController {

    private final SeasonService seasonService;
    private final ApplicationService applicationService;

    @GetMapping("")
    public ResponseEntity<SeasonListResponse> findSeasons() {
        SeasonListResponse seasonListResponse = seasonService.findSeasons();
        return ResponseEntity.ok(seasonListResponse);
    }

    @GetMapping("/{seasonId}")
    public ResponseEntity<SeasonDetailResponse> findSeason(
            @PathVariable Long seasonId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            SeasonDetailResponse response = seasonService.findSeason(seasonId, null);
            return ResponseEntity.ok(response);
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        SeasonDetailResponse response = seasonService.findSeason(seasonId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{seasonId}/slots")
    public ResponseEntity<SeasonSlotsResponse> findSeasonSlots(
            @PathVariable Long seasonId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            SeasonSlotsResponse response = seasonService.findSeasonSlots(seasonId, null);
            return ResponseEntity.ok(response);
        }
        Long userId = Long.parseLong(userDetails.getUsername());
        SeasonSlotsResponse response = seasonService.findSeasonSlots(seasonId, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{seasonId}")
    public ResponseEntity<ApplicationResponse> applyToSeason(
            @PathVariable Long seasonId,
            @Valid @RequestBody ApplicationRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ApplicationResponse response = applicationService.applyToSeason(seasonId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{seasonId}")
    public ResponseEntity<ApplicationResponse> updateApplication(
            @PathVariable Long seasonId,
            @Valid @RequestBody ApplicationModifyRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ApplicationResponse response = applicationService.updateApplication(seasonId, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{seasonId}/my-application")
    public ResponseEntity<ApplicationDetailResponse> getMyApplicationForSeason(
            @PathVariable Long seasonId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        ApplicationDetailResponse response = applicationService.getMyApplicationForSeason(seasonId, userId);
        return ResponseEntity.ok(response);
    }
}
