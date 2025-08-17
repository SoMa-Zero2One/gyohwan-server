package com.gyohwan.compass.domain;

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
}
