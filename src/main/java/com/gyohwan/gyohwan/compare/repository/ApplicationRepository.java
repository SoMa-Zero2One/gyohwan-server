package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.compare.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndSeasonId(Long userId, Long seasonId);

    @Query("SELECT COUNT(a) FROM Application a WHERE a.season.id = :seasonId")
    long countBySeasonId(@Param("seasonId") Long seasonId);
}
