package com.gyohwan.gyohwan.window.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataField extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fieldName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EntityType entityType;

    public enum EntityType {
        COUNTRY, UNIV
    }

    public DataField(String fieldName, EntityType entityType) {
        this.fieldName = fieldName;
        this.entityType = entityType;
    }
}

