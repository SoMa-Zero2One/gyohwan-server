package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.compare.domain.Application;
import com.gyohwan.gyohwan.compare.domain.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByUser(User user);

    List<Application> findBySeason(Season season);

    Optional<Application> findByUserAndSeason(User user, Season season);

    List<Application> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Application> findBySeasonIdOrderByUpdatedAtDesc(Long seasonId);

    long countBySeasonId(Long seasonId);
}

