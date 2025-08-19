package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.DomesticUniv;
import com.gyohwan.compass.domain.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, Long> {
    
    List<Season> findByDomesticUniv(DomesticUniv domesticUniv);
    
    List<Season> findByDomesticUnivOrderByStartDateDesc(DomesticUniv domesticUniv);
    
    List<Season> findByDomesticUnivIdOrderByStartDateDesc(Long domesticUnivId);
    
    Optional<Season> findByDomesticUnivAndName(DomesticUniv domesticUniv, String name);
    
    @Query("SELECT s FROM Season s WHERE s.domesticUniv = :domesticUniv AND s.startDate <= :now AND s.endDate >= :now")
    Optional<Season> findCurrentSeason(@Param("domesticUniv") DomesticUniv domesticUniv, @Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM Season s WHERE s.domesticUniv = :domesticUniv AND s.startDate > :now ORDER BY s.startDate ASC")
    List<Season> findUpcomingSeasons(@Param("domesticUniv") DomesticUniv domesticUniv, @Param("now") LocalDateTime now);
    
    @Query("SELECT s FROM Season s WHERE s.startDate <= :now AND s.endDate >= :now")
    List<Season> findAllCurrentSeasons(@Param("now") LocalDateTime now);
}
