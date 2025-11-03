package com.gyohwan.gyohwan.community.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Lob // 긴 텍스트
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private boolean isAnonymous;

    private String guestNickname;
    private String guestPassword;

    @Column(nullable = true)
    private String countryCode;

    @Column(nullable = true)
    private Long outgoingUnivId;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public Post(String title, String content, User user, boolean isAnonymous,
                String guestNickname, String guestPassword,
                String countryCode, Long outgoingUnivId) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.guestNickname = guestNickname;
        this.guestPassword = guestPassword;
        this.countryCode = countryCode;
        this.outgoingUnivId = outgoingUnivId;
    }


    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}


