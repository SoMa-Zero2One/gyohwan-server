package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.OutgoingUniv;
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
    
    List<OutgoingUniv> findByCountry(String country);
    
    @Query("SELECT o FROM OutgoingUniv o WHERE o.nameEn LIKE %:keyword% OR o.nameKo LIKE %:keyword%")
    List<OutgoingUniv> findByNameContaining(@Param("keyword") String keyword);
    
    List<OutgoingUniv> findByCountryOrderByNameEnAsc(String country);
}
