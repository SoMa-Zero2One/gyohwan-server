package com.gyohwan.compass.repository;

import com.gyohwan.compass.domain.Gpa;
import com.gyohwan.compass.domain.Language;
import com.gyohwan.compass.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    
    List<Language> findByUser(User user);
    
    List<Language> findByUserOrderByModifiedDateDesc(User user);
    
    List<Language> findByUserIdOrderByModifiedDateDesc(Long userId);
    
    List<Language> findByTestType(Language.TestType testType);
    
    List<Language> findByVerifyStatus(Gpa.VerifyStatus verifyStatus);
    
    Optional<Language> findByUserAndTestType(User user, Language.TestType testType);
    
    @Query("SELECT l FROM Language l WHERE l.user = :user AND l.verifyStatus = 'APPROVED' ORDER BY l.modifiedDate DESC")
    List<Language> findApprovedLanguagesByUser(@Param("user") User user);
    
    @Query("SELECT l FROM Language l WHERE l.verifyStatus = 'PENDING' ORDER BY l.createdDate ASC")
    List<Language> findPendingLanguagesOrderByCreatedDate();
    
    List<Language> findByUserAndTestTypeOrderByModifiedDateDesc(User user, Language.TestType testType);
}
