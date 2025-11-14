package com.gyohwan.gyohwan.common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Continent extends BaseEntity {

    @Id
    @Column(name = "continent_code", nullable = false, unique = true)
    private String code; // 대륙 코드 (ASIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA, AFRICA, OCEANIA, ANTARCTICA)

    @Column(nullable = false)
    private String nameKo;

    @Column(nullable = false)
    private String nameEn;

    @OneToMany(mappedBy = "continent")
    private List<Country> countries = new ArrayList<>();

    public Continent(String code, String nameKo, String nameEn) {
        this.code = code;
        this.nameKo = nameKo;
        this.nameEn = nameEn;
    }
}
