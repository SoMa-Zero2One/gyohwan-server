package com.gyohwan.compass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private Slot slot;

    @Column(nullable = false)
    private Integer choice;

    private Double gpaScore;

    @Enumerated(EnumType.STRING)
    private Gpa.Criteria gpaCriteria;

    private String languageScore;

    private String languageGrade;

    @Enumerated(EnumType.STRING)
    private Language.TestType languageTest;

    private Double score;

    // 생성자
    public Choice(Application application, Slot slot, Integer choice) {
        this.application = application;
        this.slot = slot;
        this.choice = choice;
    }
}
