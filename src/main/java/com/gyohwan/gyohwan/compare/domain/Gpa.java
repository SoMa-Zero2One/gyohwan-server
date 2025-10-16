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
        _4_5("4.5"),
        _4_3("4.3"),
        _4_0("4.0");

        private final String value;

        Criteria(String value) {
            this.value = value;
        }

        @com.fasterxml.jackson.annotation.JsonValue
        public String getValue() {
            return value;
        }

        @com.fasterxml.jackson.annotation.JsonCreator
        public static Criteria from(String value) {
            for (Criteria criteria : Criteria.values()) {
                if (criteria.value.equals(value)) {
                    return criteria;
                }
            }
            throw new IllegalArgumentException("Unknown enum value: " + value);
        }
    }

    public enum VerifyStatus {
        PENDING, APPROVED, REJECTED
    }

    public Gpa(User user, Double score, Criteria criteria) {
        this.user = user;
        this.score = score;
        this.criteria = criteria;
        this.verifyStatus = VerifyStatus.APPROVED;
    }
}
