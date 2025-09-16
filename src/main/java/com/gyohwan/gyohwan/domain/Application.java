package com.gyohwan.gyohwan.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Application extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "season_id", nullable = false)
    private Season season;

    private Integer modifyCount;

    @Column(length = 20)
    private String nickname;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("choice asc")
    @BatchSize(size = 10)
    private List<Choice> choices = new ArrayList<>();

    // 생성자
    public Application(User user, Season season, String nickname) {
        this.user = user;
        this.season = season;
        this.nickname = nickname;
        this.modifyCount = 0;
    }

    // 수정 횟수 증가
    public void incrementModifyCount() {
        this.modifyCount = (this.modifyCount == null ? 0 : this.modifyCount) + 1;
    }
}
