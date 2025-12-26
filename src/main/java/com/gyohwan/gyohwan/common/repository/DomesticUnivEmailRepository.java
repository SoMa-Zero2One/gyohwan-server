package com.gyohwan.gyohwan.common.repository;

import com.gyohwan.gyohwan.common.domain.DomesticUnivEmail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DomesticUnivEmailRepository extends JpaRepository<DomesticUnivEmail, Long> {

    @Query("SELECT due FROM DomesticUnivEmail due JOIN FETCH due.domesticUniv WHERE due.emailDomain = :emailDomain")
    Optional<DomesticUnivEmail> findByEmailDomainWithUniv(@Param("emailDomain") String emailDomain);

    Optional<DomesticUnivEmail> findByEmailDomain(String emailDomain);

    List<DomesticUnivEmail> findByDomesticUnivId(Long domesticUnivId);

    boolean existsByEmailDomain(String emailDomain);
}

