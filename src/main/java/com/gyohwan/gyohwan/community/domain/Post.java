package com.gyohwan.gyohwan.community.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.Country;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

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

    @Lob
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    private boolean isAnonymous;

    private String guestNickname;
    private String guestPassword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_code", referencedColumnName = "country_code", nullable = true)
    private Country country;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outgoing_univ_id", referencedColumnName = "id", nullable = true)
    private OutgoingUniv outgoingUniv;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 100)
    private List<PostLike> postLikes = new ArrayList<>();

    public Post(String title, String content, User user, boolean isAnonymous,
                String guestNickname, String guestPassword,
                Country country, OutgoingUniv outgoingUniv) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.isAnonymous = isAnonymous;
        this.guestNickname = guestNickname;
        this.guestPassword = guestPassword;
        this.country = country;
        this.outgoingUniv = outgoingUniv;
    }


    public void update(String title, String content, Boolean isAnonymous) {
        this.title = title;
        this.content = content;
        if (isAnonymous != null) {
            this.isAnonymous = isAnonymous;
        }
    }
}


