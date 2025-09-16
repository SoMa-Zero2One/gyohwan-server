package com.gyohwan.gyohwan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gpa extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Criteria criteria;

    @Column(columnDefinition = "TEXT")
    private String document;

    @Enumerated(EnumType.STRING)
    private VerifyStatus verifyStatus;

    private String statusReason;

    public enum Criteria {
        _4_5, _4_3, _4_0
    }

    public enum VerifyStatus {
        PENDING, APPROVED, REJECTED
    }

    // 생성자
    public Gpa(User user, Double score, Criteria criteria) {
        this.user = user;
        this.score = score;
        this.criteria = criteria;
        this.verifyStatus = VerifyStatus.PENDING;
    }
}
