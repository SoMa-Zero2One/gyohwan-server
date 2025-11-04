package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutgoingUnivRepository extends JpaRepository<OutgoingUniv, Long> {

    Optional<OutgoingUniv> findByNameEn(String nameEn);

    Optional<OutgoingUniv> findByNameKo(String nameKo);

    List<OutgoingUniv> findByCountry(Country country);

    @Query("SELECT o FROM OutgoingUniv o WHERE o.nameEn LIKE %:keyword% OR o.nameKo LIKE %:keyword%")
    List<OutgoingUniv> findByNameContaining(@Param("keyword") String keyword);

    List<OutgoingUniv> findByCountryOrderByNameEnAsc(Country country);

    @Query("SELECT DISTINCT o FROM OutgoingUniv o " +
            "JOIN Slot s ON s.outgoingUniv.id = o.id " +
            "WHERE s.season.id = :seasonId")
    List<OutgoingUniv> findBySeasonId(@Param("seasonId") Long seasonId);
}

