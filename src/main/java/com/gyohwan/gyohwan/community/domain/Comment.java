package com.gyohwan.gyohwan.community.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private boolean isAnonymous;

    private String guestNickname;
    private String guestPassword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;


    public Comment(String content, User user, boolean isAnonymous,
                   String guestNickname, String guestPassword, Post post) {
        this.content = content;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.guestNickname = guestNickname;
        this.guestPassword = guestPassword;
        this.post = post;
    }
}
