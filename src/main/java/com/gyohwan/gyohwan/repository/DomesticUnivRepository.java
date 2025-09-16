package com.gyohwan.gyohwan.repository;

import com.gyohwan.gyohwan.domain.DomesticUniv;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DomesticUnivRepository extends JpaRepository<DomesticUniv, Long> {

    Optional<DomesticUniv> findByCode(String code);

    Optional<DomesticUniv> findByName(String name);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}

