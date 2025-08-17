package com.gyohwan.compass.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Slot extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outgoing_univ_id", nullable = false)
    private OutgoingUniv outgoingUniv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    @Column(nullable = false)
    private String name;

    private Integer slotCount;

    @Enumerated(EnumType.STRING)
    private Duration duration;

    private String etc;

    public enum Duration {
        SEMESTER, YEAR
    }
}
