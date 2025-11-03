package com.gyohwan.gyohwan.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

    public Country(String code, String nameKo, String nameEn) {
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
    }
}

