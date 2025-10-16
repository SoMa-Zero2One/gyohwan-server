package com.gyohwan.gyohwan.compare.controller;

import com.gyohwan.gyohwan.compare.dto.ApplicationRequest;
import com.gyohwan.gyohwan.compare.dto.ApplicationResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonDetailResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonListResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonSlotsResponse;
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
            @PathVariable Long seasonId
    ) {
        SeasonDetailResponse response = seasonService.findSeason(seasonId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{seasonId}/slots")
    public ResponseEntity<SeasonSlotsResponse> findSeasonSlots(
            @PathVariable Long seasonId
    ) {
        SeasonSlotsResponse response = seasonService.findSeasonSlots(seasonId);
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
}
