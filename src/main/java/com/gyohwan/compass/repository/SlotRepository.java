package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.OutgoingUniv;
import com.gyohwan.compass.domain.Season;
import com.gyohwan.compass.domain.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    
    List<Slot> findByOutgoingUniv(OutgoingUniv outgoingUniv);
    
    List<Slot> findBySeason(Season season);
    
    List<Slot> findBySeasonOrderByNameAsc(Season season);
    
    List<Slot> findByOutgoingUnivAndSeason(OutgoingUniv outgoingUniv, Season season);
    
    Optional<Slot> findByOutgoingUnivAndSeasonAndName(OutgoingUniv outgoingUniv, Season season, String name);
    
    List<Slot> findByDuration(Slot.Duration duration);
    
    @Query("SELECT s FROM Slot s WHERE s.season = :season AND s.outgoingUniv.country = :country ORDER BY s.outgoingUniv.nameEn ASC, s.name ASC")
    List<Slot> findBySeasonAndCountryOrderByUnivAndName(@Param("season") Season season, @Param("country") String country);
    
    @Query("SELECT s FROM Slot s WHERE s.season = :season AND s.outgoingUniv.nameEn LIKE %:keyword% OR s.outgoingUniv.nameKo LIKE %:keyword% OR s.name LIKE %:keyword%")
    List<Slot> findBySeasonAndKeyword(@Param("season") Season season, @Param("keyword") String keyword);
    
    List<Slot> findBySeasonIdOrderByNameAsc(Long seasonId);
}
