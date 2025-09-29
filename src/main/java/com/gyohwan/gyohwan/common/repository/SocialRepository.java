package com.gyohwan.gyohwan.common.repository;

import com.gyohwan.gyohwan.common.domain.Social;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialRepository extends JpaRepository<Social, Long> {

    Optional<Social> findByExternalId(String externalId);
}
