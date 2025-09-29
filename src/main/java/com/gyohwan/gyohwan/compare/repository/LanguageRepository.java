package com.gyohwan.gyohwan.compare.repository;

import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.compare.domain.Gpa;
import com.gyohwan.gyohwan.compare.domain.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {

    List<Language> findByUser(User user);

    List<Language> findByUserOrderByUpdatedAtDesc(User user);

    List<Language> findByUserIdOrderByUpdatedAtDesc(Long userId);

    List<Language> findByTestType(Language.TestType testType);

    List<Language> findByVerifyStatus(Gpa.VerifyStatus verifyStatus);

    Optional<Language> findByUserAndTestType(User user, Language.TestType testType);

    @Query("SELECT l FROM Language l WHERE l.user = :user AND l.verifyStatus = 'APPROVED' ORDER BY l.updatedAt DESC")
    List<Language> findApprovedLanguagesByUser(@Param("user") User user);

    @Query("SELECT l FROM Language l WHERE l.verifyStatus = 'PENDING' ORDER BY l.createdAt ASC")
    List<Language> findPendingLanguagesOrderByCreatedAt();

    List<Language> findByUserAndTestTypeOrderByUpdatedAtDesc(User user, Language.TestType testType);
}

