package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.compare.domain.Season;
import com.gyohwan.gyohwan.compare.dto.SeasonDetailResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonListResponse;
import com.gyohwan.gyohwan.compare.repository.SeasonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;

    public SeasonListResponse findSeasons() {
        List<Season> seasons = seasonRepository.findAll();
        return SeasonListResponse.from(seasons);
    }

    @Transactional(readOnly = true)
    public SeasonDetailResponse findSeason(Long seasonId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new IllegalArgumentException("Season not found"));

        return new SeasonDetailResponse(
                season.getId(),
                season.getDomesticUniv().getName(),
                "",
                season.getName(),
                season.getStartDate(),
                season.getEndDate(),
                false,
                10
        );
    }
}
