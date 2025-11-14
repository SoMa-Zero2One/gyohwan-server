package com.gyohwan.gyohwan.common.repository;

import com.gyohwan.gyohwan.common.domain.Continent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContinentRepository extends JpaRepository<Continent, String> {
    
    boolean existsByCode(String code);
}

