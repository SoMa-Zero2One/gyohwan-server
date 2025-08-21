package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.Application;
import com.gyohwan.compass.domain.Choice;
import com.gyohwan.compass.domain.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    
    List<Choice> findByApplicationOrderByChoiceAsc(Application application);
    
    List<Choice> findBySlot(Slot slot);
    
    Optional<Choice> findByApplicationAndChoice(Application application, Integer choice);
    
    @Query("SELECT c FROM Choice c WHERE c.application = :application ORDER BY c.choice ASC")
    List<Choice> findByApplicationOrderByRank(@Param("application") Application application);
    
    @Query("SELECT c FROM Choice c WHERE c.slot = :slot ORDER BY c.score DESC")
    List<Choice> findBySlotOrderByScoreDesc(@Param("slot") Slot slot);
    
    List<Choice> findByApplicationIdOrderByChoiceAsc(Long applicationId);

    @Query("SELECT c FROM Choice c JOIN FETCH c.application a JOIN FETCH a.user WHERE c.slot = :slot ORDER BY c.choice ASC")
    List<Choice> findBySlotWithApplicationAndUserOrderByChoiceAsc(@Param("slot") Slot slot);
}

