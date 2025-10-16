package com.gyohwan.gyohwan.compare.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.User;
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

    private Double extraScore;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("choice asc")
    @BatchSize(size = 10)
    private List<Choice> choices = new ArrayList<>();

    // 생성자
    public Application(User user, Season season, String nickname, Double extraScore, int modifyCount) {
        this.user = user;
        this.season = season;
        this.nickname = nickname;
        this.extraScore = extraScore;
        this.modifyCount = modifyCount;
    }

    // Choice 추가 메서드
    public void addChoice(Choice choice) {
        this.choices.add(choice);
    }

    // 수정 횟수 증가
    public void incrementModifyCount() {
        this.modifyCount = (this.modifyCount == null ? 0 : this.modifyCount) + 1;
    }
}
