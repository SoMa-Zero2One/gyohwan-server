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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class SeasonService {

    private final SeasonRepository seasonRepository;
    private final SlotRepository slotRepository;
    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    /**
     * 시즌 목록 조회
     * @param expired null이면 전체, true면 종료된 시즌만, false면 진행중/예정 시즌만
     */
    public SeasonListResponse findSeasons(Boolean expired) {
        List<Season> seasons;
        
        if (expired == null) {
            // 전체 조회
            seasons = seasonRepository.findAll();
        } else if (expired) {
            // 종료된 시즌만 조회
            seasons = seasonRepository.findExpiredSeasons(LocalDateTime.now());
        } else {
            // 진행중 또는 예정된 시즌만 조회
            seasons = seasonRepository.findActiveSeasons(LocalDateTime.now());
        }
        
        log.info("Retrieved seasons. Expired filter: {}, Count: {}", expired, seasons.size());
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

        log.info("Checked if user {} applied to season {}", userId, seasonId);
        return new SeasonDetailResponse(
                season.getId(),
                season.getDomesticUniv().getName(),
                "",
                season.getName(),
                season.getStartDate(),
                season.getEndDate(),
                hasApplied,
                season.getParticipantCount() != null ? season.getParticipantCount() : 0,
                season.getOpenchatUrl()
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

        log.info("User {} retrieved slot information for season {}", userId, seasonId);
        return SeasonSlotsResponse.from(season, slots, hasApplied, season.getParticipantCount() != null ? season.getParticipantCount() : 0);
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

        log.info("User {} checked eligibility for season {}", userId, seasonId);
        return new SeasonEligibilityResponse(true, "지원 가능합니다.");
    }
}
