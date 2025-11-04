package com.gyohwan.gyohwan.window.repository;

import com.gyohwan.gyohwan.window.domain.UnivFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnivFavoriteRepository extends JpaRepository<UnivFavorite, Long> {

    boolean existsByUserIdAndOutgoingUnivId(Long userId, Long outgoingUnivId);

    Optional<UnivFavorite> findByUserIdAndOutgoingUnivId(Long userId, Long outgoingUnivId);

    List<UnivFavorite> findByUserId(Long userId);
}

