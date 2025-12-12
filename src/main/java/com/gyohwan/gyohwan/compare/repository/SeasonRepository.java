package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.compare.domain.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SeasonRepository extends JpaRepository<Season, Long> {
    
    /**
     * endDate가 현재 시간 이후인 시즌들 조회 (진행중 또는 예정)
     */
    @Query("SELECT s FROM Season s WHERE s.endDate > :currentTime")
    List<Season> findActiveSeasons(@Param("currentTime") LocalDateTime currentTime);
    
    /**
     * endDate가 현재 시간 이전인 시즌들 조회 (종료됨)
     */
    @Query("SELECT s FROM Season s WHERE s.endDate <= :currentTime")
    List<Season> findExpiredSeasons(@Param("currentTime") LocalDateTime currentTime);
}
