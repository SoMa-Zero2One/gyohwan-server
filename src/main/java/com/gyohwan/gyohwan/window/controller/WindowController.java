package com.gyohwan.gyohwan.window.controller;

import com.gyohwan.gyohwan.window.dto.CountryDetailResponse;
import com.gyohwan.gyohwan.window.dto.CountryListResponse;
import com.gyohwan.gyohwan.window.dto.UnivDetailResponse;
import com.gyohwan.gyohwan.window.dto.UnivListResponse;
import com.gyohwan.gyohwan.window.service.FavoriteService;
import com.gyohwan.gyohwan.window.service.WindowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/windows")
@RestController
public class WindowController {

    private final WindowService windowService;
    private final FavoriteService favoriteService;

    /**
     * 모든 국가 목록 조회
     */
    @GetMapping("/countries")
    public ResponseEntity<List<CountryListResponse>> findAllCountries() {
        List<CountryListResponse> response = windowService.findAllCountries();
        return ResponseEntity.ok(response);
    }

    /**
     * 국가 상세 조회 (대학 목록 포함)
     */
    @GetMapping("/countries/{countryCode}")
    public ResponseEntity<CountryDetailResponse> findCountry(
            @PathVariable String countryCode
    ) {
        CountryDetailResponse response = windowService.findCountry(countryCode);
        return ResponseEntity.ok(response);
    }

    /**
     * 모든 대학 목록 조회 (또는 시즌별 필터링)
     */
    @GetMapping("/outgoing-universities")
    public ResponseEntity<List<UnivListResponse>> findUniversities(
            @RequestParam(required = false) Long seasons,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = userDetails != null ? Long.parseLong(userDetails.getUsername()) : null;

        List<UnivListResponse> response;
        if (seasons != null) {
            response = windowService.findUniversitiesBySeason(seasons, userId);
        } else {
            response = windowService.findAllUniversities(userId);
        }

        return ResponseEntity.ok(response);
    }

    /**
     * 대학 상세 조회
     */
    @GetMapping("/outgoing-universities/{universityId}")
    public ResponseEntity<UnivDetailResponse> findUniversity(
            @PathVariable Long universityId
    ) {
        UnivDetailResponse response = windowService.findUniversity(universityId);
        return ResponseEntity.ok(response);
    }

    /**
     * 대학 즐겨찾기 추가
     */
    @PostMapping("/outgoing-universities/{universityId}/favorite")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long universityId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.addFavorite(universityId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 대학 즐겨찾기 삭제
     */
    @DeleteMapping("/outgoing-universities/{universityId}/favorite")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long universityId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = Long.parseLong(userDetails.getUsername());
        favoriteService.removeFavorite(universityId, userId);
        return ResponseEntity.noContent().build();
    }
}

