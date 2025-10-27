package com.gyohwan.gyohwan.compare.service;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.common.exception.CustomException;
import com.gyohwan.gyohwan.common.exception.ErrorCode;
import com.gyohwan.gyohwan.common.repository.UserRepository;
import com.gyohwan.gyohwan.compare.domain.Application;
import com.gyohwan.gyohwan.compare.domain.Season;
import com.gyohwan.gyohwan.compare.domain.Slot;
import com.gyohwan.gyohwan.compare.dto.SeasonDetailResponse;
import com.gyohwan.gyohwan.compare.dto.SeasonEligibilityResponse;
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
    private final UserRepository userRepository;

    public SeasonListResponse findSeasons() {
        List<Season> seasons = seasonRepository.findAll();
        return SeasonListResponse.from(seasons);
    }

    @Transactional(readOnly = true)
    public SeasonDetailResponse findSeason(Long seasonId, Long userId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEASON_NOT_FOUND));

        if (season.getDomesticUniv() == null) {
            throw new CustomException(ErrorCode.SEASON_DATA_INCOMPLETE);
        }

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

        long applicantCount = applicationRepository.countBySeasonId(seasonId);

        return SeasonSlotsResponse.from(season, slots, hasApplied, applicantCount);
    }

    @Transactional(readOnly = true)
    public SeasonEligibilityResponse checkEligibility(Long seasonId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEASON_NOT_FOUND));

        // 학교 인증 여부 확인
        boolean schoolVerified = user.getSchoolVerified() != null && user.getSchoolVerified();
        if (!schoolVerified || user.getDomesticUniv() == null) {
            throw new CustomException(ErrorCode.SCHOOL_NOT_VERIFIED);
        }

        // 사용자의 학교와 시즌의 학교가 일치하는지 확인
        boolean univMatch = user.getDomesticUniv().getId().equals(season.getDomesticUniv().getId());
        if (!univMatch) {
            throw new CustomException(ErrorCode.SEASON_SCHOOL_MISMATCH);
        }

        // 이미 지원했는지 확인
        boolean alreadyApplied = applicationRepository.existsByUserIdAndSeasonId(userId, seasonId);
        if (alreadyApplied) {
            throw new CustomException(ErrorCode.ALREADY_APPLIED);
        }

        return new SeasonEligibilityResponse(true, "지원 가능합니다.");
    }
}
