package com.gyohwan.gyohwan.compare.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
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
    
    public Choice(Application application, Slot slot, Integer choice, Gpa gpa, Language language) {
        this.application = application;
        this.slot = slot;
        this.choice = choice;

        if (gpa != null) {
            this.gpaScore = gpa.getScore();
            this.gpaCriteria = gpa.getCriteria();
        }

        if (language != null) {
            this.languageScore = language.getScore();
            this.languageGrade = language.getGrade();
            this.languageTest = language.getTestType();
        }
    }
}
