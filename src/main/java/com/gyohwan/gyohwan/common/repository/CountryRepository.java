package com.gyohwan.gyohwan.common.repository;

import com.gyohwan.gyohwan.common.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {

    Optional<Country> findByCode(String code);

    boolean existsByCode(String code);
}

