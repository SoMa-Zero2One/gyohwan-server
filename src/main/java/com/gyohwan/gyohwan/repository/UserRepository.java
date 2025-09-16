package com.gyohwan.gyohwan.repository;

import com.gyohwan.gyohwan.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUuid(String uuid);

    Optional<User> findByEmail(String email);

    boolean existsByUuid(String uuid);

    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.applications a LEFT JOIN FETCH a.season WHERE u.id = :id")
    Optional<User> findByIdWithApplications(@Param("id") Long id);
}
