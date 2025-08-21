package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.Gpa;
import com.gyohwan.compass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GpaRepository extends JpaRepository<Gpa, Long> {
    
    List<Gpa> findByUser(User user);
    
    List<Gpa> findByUserOrderByUpdatedAtDesc(User user);
    
    List<Gpa> findByUserIdOrderByUpdatedAtDesc(Long userId);
    
    List<Gpa> findByCriteria(Gpa.Criteria criteria);
    
    List<Gpa> findByVerifyStatus(Gpa.VerifyStatus verifyStatus);
    
    Optional<Gpa> findByUserAndCriteria(User user, Gpa.Criteria criteria);
    
    @Query("SELECT g FROM Gpa g WHERE g.user = :user AND g.verifyStatus = 'APPROVED' ORDER BY g.updatedAt DESC")
    List<Gpa> findApprovedGpasByUser(@Param("user") User user);
    
    @Query("SELECT g FROM Gpa g WHERE g.verifyStatus = 'PENDING' ORDER BY g.createdAt ASC")
    List<Gpa> findPendingGpasOrderByCreatedAt();
}

