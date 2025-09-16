package com.gyohwan.gyohwan.repository;

import com.gyohwan.gyohwan.domain.OutgoingUniv;
import com.gyohwan.gyohwan.domain.Season;
import com.gyohwan.gyohwan.domain.Slot;
import com.gyohwan.gyohwan.repository.dto.SlotWithApplicantCountDto;
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

    @Query("SELECT s FROM Slot s WHERE s.season = :season AND (s.outgoingUniv.nameEn LIKE %:keyword% OR s.outgoingUniv.nameKo LIKE %:keyword% OR s.name LIKE %:keyword%)")
    List<Slot> findBySeasonAndKeyword(@Param("season") Season season, @Param("keyword") String keyword);

    List<Slot> findBySeasonIdOrderByNameAsc(Long seasonId);

    @Query("SELECT new com.gyohwan.gyohwan.repository.dto.SlotWithApplicantCountDto(s, COUNT(c.id)) " +
            "FROM Slot s LEFT JOIN Choice c ON c.slot = s " +
            "GROUP BY s.id")
    List<SlotWithApplicantCountDto> findAllWithApplicantCounts();

    @Query("SELECT s FROM Slot s JOIN FETCH s.outgoingUniv WHERE s.id = :id")
    Optional<Slot> findByIdWithOutgoingUniv(@Param("id") Long id);

    @Query("SELECT new com.gyohwan.gyohwan.repository.dto.SlotWithApplicantCountDto(s, COUNT(c.id)) " +
            "FROM Slot s LEFT JOIN Choice c ON c.slot = s " +
            "WHERE s.season.id = :seasonId " +
            "GROUP BY s.id")
    List<SlotWithApplicantCountDto> findBySeasonWithApplicantCounts(@Param("seasonId") Long seasonId);

    @Query("SELECT s FROM Slot s JOIN FETCH s.outgoingUniv WHERE s.id = :id AND s.season.id = :seasonId")
    Optional<Slot> findByIdAndSeasonWithOutgoingUniv(@Param("id") Long id, @Param("seasonId") Long seasonId);
}

