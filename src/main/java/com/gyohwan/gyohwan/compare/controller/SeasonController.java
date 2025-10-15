package com.gyohwan.gyohwan.compare.controller;

import com.gyohwan.gyohwan.compare.dto.SeasonDetailResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonListResponse;
import com.gyohwan.gyohwan.compare.service.SeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/seasons")
@RestController
public class SeasonController {

    private final SeasonService seasonService;

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
}
