package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.Application;
import com.gyohwan.compass.domain.Season;
import com.gyohwan.compass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    List<Application> findByUser(User user);
    
    List<Application> findBySeason(Season season);
    
    Optional<Application> findByUserAndSeason(User user, Season season);
    
    List<Application> findByUserIdOrderByModifiedDateDesc(Long userId);
    
    List<Application> findBySeasonIdOrderByModifiedDateDesc(Long seasonId);
}
