package com.gyohwan.gyohwan.common.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public User getUser() {
        return user;
    }

    public Social(User user, SocialType socialCode, String externalId) {
        this.user = user;
        this.socialCode = socialCode;
        this.externalId = externalId;
    }
}
