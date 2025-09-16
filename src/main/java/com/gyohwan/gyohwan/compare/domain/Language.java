package com.gyohwan.gyohwan.compare.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Language extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String score;

    private String grade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestType testType;

    @Column(columnDefinition = "TEXT")
    private String document;

    @Enumerated(EnumType.STRING)
    private Gpa.VerifyStatus verifyStatus;

    private String statusReason;

    public enum TestType {
        TOEFL, IELTS, TOEIC, HSK, JLPT
    }

    // 생성자 (언어 정보를 문자열로 임시 저장)
    public Language(User user, String languageInfo) {
        this.user = user;
        this.score = languageInfo; // 전체 언어 정보를 score 필드에 임시 저장
        this.testType = TestType.TOEIC; // 임시값
        this.verifyStatus = Gpa.VerifyStatus.PENDING;
    }
}
