package com.gyohwan.gyohwan.common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "domestic_univ_email",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_email_domain",
                        columnNames = {"email_domain"}
                )
        }
)
public class DomesticUnivEmail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domestic_univ_id", nullable = false)
    private DomesticUniv domesticUniv;

    @Column(name = "email_domain", nullable = false, length = 100)
    private String emailDomain;

    public DomesticUnivEmail(DomesticUniv domesticUniv, String emailDomain) {
        this.domesticUniv = domesticUniv;
        this.emailDomain = emailDomain;
    }
}

