package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.compare.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    boolean existsByUserIdAndSeasonId(Long userId, Long seasonId);

    Optional<Application> findByUserIdAndSeasonId(Long userId, Long seasonId);
    

    @Query("SELECT COUNT(a) FROM Application a WHERE a.season.id = :seasonId")
    long countBySeasonId(@Param("seasonId") Long seasonId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.season " +
            "LEFT JOIN FETCH a.choices c " +
            "LEFT JOIN FETCH c.slot s " +
            "LEFT JOIN FETCH s.outgoingUniv " +
            "WHERE a.user.id = :userId " +
            "ORDER BY a.createdAt DESC")
    Optional<Application> findLatestByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.season " +
            "LEFT JOIN FETCH a.choices c " +
            "LEFT JOIN FETCH c.slot s " +
            "LEFT JOIN FETCH s.outgoingUniv " +
            "WHERE a.id = :applicationId")
    Optional<Application> findByIdWithDetails(@Param("applicationId") Long applicationId);

    @Query("SELECT a FROM Application a " +
            "LEFT JOIN FETCH a.season " +
            "LEFT JOIN FETCH a.choices c " +
            "LEFT JOIN FETCH c.slot s " +
            "LEFT JOIN FETCH s.outgoingUniv " +
            "WHERE a.user.id = :userId AND a.season.id = :seasonId")
    Optional<Application> findByUserIdAndSeasonIdWithDetails(@Param("userId") Long userId, @Param("seasonId") Long seasonId);
}
