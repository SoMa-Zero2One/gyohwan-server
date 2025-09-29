package com.gyohwan.gyohwan.common.domain;

import jakarta.persistence.*;

@Entity
public class Social {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialCode;

    private String externalId;
    
    private String accessToken;
}
