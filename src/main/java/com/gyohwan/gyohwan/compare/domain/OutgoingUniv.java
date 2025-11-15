package com.gyohwan.gyohwan.compare.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.Country;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OutgoingUniv extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nameEn;

    private String nameKo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = true)
    private Country country;

    private String logoUrl;

    private String homepageUrl;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String information;
}
