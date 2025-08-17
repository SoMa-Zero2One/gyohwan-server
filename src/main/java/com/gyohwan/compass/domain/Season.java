package com.gyohwan.compass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Season extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domestic_univ_id", nullable = false)
    private DomesticUniv domesticUniv;

    @Column(nullable = false)
    private String name;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Double maxScore;

    @Column(columnDefinition = "integer default 4")
    private Integer defaultModifyCount;
}
