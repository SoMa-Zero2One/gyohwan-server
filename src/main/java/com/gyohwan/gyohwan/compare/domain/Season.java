package com.gyohwan.gyohwan.compare.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.DomesticUniv;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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

    @OneToMany(mappedBy = "season", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime expiredDate;

    private Double maxScore;

    @Column(columnDefinition = "integer default 4")
    private Integer defaultModifyCount;

    private String openchatUrl;

    @Column(columnDefinition = "integer default 0")
    private Integer participantCount = 0;

    // participantCount 설정 (실시간 count 값 할당용)
    public void setParticipantCount(Integer participantCount) {
        this.participantCount = participantCount;
    }
}
