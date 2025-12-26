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
public class DomesticUniv extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @OneToMany(mappedBy = "domesticUniv", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DomesticUnivEmail> emailDomains = new ArrayList<>();

    @Column(length = 2048)
    private String logoUrl;

}
