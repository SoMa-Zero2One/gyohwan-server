package com.gyohwan.gyohwan.common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Country extends BaseEntity {

    @Id
    @Column(name = "country_code", nullable = false, unique = true)
    private String code; // ISO 3166-1 alpha-2 코드

    @Column(nullable = false)
    private String nameKo;

    @Column(nullable = false)
    private String nameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "continent_code")
    private Continent continent;

    public Country(String code, String nameKo, String nameEn) {
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
    }

    public Country(String code, String nameKo, String nameEn, Continent continent) {
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
        this.continent = continent;
    }

    public void setContinent(Continent continent) {
        this.continent = continent;
    }
}

