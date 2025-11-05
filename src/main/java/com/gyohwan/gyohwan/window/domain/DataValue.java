package com.gyohwan.gyohwan.window.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private DataField field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", nullable = true)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outgoing_univ_id", nullable = true)
    private OutgoingUniv outgoingUniv;

    @Column(nullable = false, length = 2048)
    private String value;

    public DataValue(DataField field, Country country, String value) {
        this.field = field;
        this.country = country;
        this.value = value;
    }

    public DataValue(DataField field, OutgoingUniv outgoingUniv, String value) {
        this.field = field;
        this.outgoingUniv = outgoingUniv;
        this.value = value;
    }
}

