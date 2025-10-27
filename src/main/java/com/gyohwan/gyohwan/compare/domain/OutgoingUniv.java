package com.gyohwan.gyohwan.compare.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
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

    private String country;

    private String logoUrl;
}
