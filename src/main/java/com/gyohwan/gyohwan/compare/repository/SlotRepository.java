package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.compare.domain.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, Long> {
    
    @Query("SELECT s FROM Slot s " +
           "LEFT JOIN FETCH s.outgoingUniv " +
           "LEFT JOIN FETCH s.choices " +
           "WHERE s.season.id = :seasonId")
    List<Slot> findBySeasonIdWithOutgoingUnivAndChoices(@Param("seasonId") Long seasonId);
}

