package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.compare.domain.Application;
import com.gyohwan.gyohwan.compare.domain.Season;
import com.gyohwan.gyohwan.compare.domain.Slot;
import com.gyohwan.gyohwan.compare.dto.SeasonDetailResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonListResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonSlotsResponse;
import com.gyohwan.gyohwan.compare.repository.ApplicationRepository;
import com.gyohwan.gyohwan.compare.repository.SeasonRepository;
import com.gyohwan.gyohwan.compare.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;
    private final ApplicationRepository applicationRepository;

    public SeasonListResponse findSeasons() {
        List<Season> seasons = seasonRepository.findAll();
        return SeasonListResponse.from(seasons);
    }

    @Transactional(readOnly = true)
    public SeasonDetailResponse findSeason(Long seasonId, Long userId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new IllegalArgumentException("Season not found"));

        Application application = applicationRepository.findByUserIdAndSeasonId(userId, seasonId)
                .orElse(null);

        boolean hasApplied = application != null;

        long applicationCount = applicationRepository.countBySeasonId(seasonId);

        return new SeasonDetailResponse(
                season.getId(),
                season.getDomesticUniv().getName(),
                "",
                season.getName(),
                season.getStartDate(),
                season.getEndDate(),
                hasApplied,
                applicationCount
        );
    }

    @Transactional(readOnly = true)
    public SeasonSlotsResponse findSeasonSlots(Long seasonId, Long userId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEASON_NOT_FOUND));

        Application application = applicationRepository.findByUserIdAndSeasonId(userId, seasonId)
                .orElse(null);
        boolean hasApplied = application != null;

        List<Slot> slots = slotRepository.findBySeasonIdWithOutgoingUnivAndChoices(seasonId);

        return SeasonSlotsResponse.from(season, slots, hasApplied);
    }
}
