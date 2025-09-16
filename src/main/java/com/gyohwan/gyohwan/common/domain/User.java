package com.gyohwan.gyohwan.common.domain;

import com.gyohwan.gyohwan.compare.domain.Application;
import com.gyohwan.gyohwan.compare.domain.Gpa;
import com.gyohwan.gyohwan.compare.domain.Language;
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
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    private String email;

    private String nickname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "domestic_univ_id", nullable = false)
    private DomesticUniv domesticUniv;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Gpa> gpas = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Language> languages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 10)
    private List<Application> applications = new ArrayList<>();

    // 생성자
    public User(String uuid, String email, String nickname, DomesticUniv domesticUniv) {
        this.uuid = uuid;
        this.email = email;
        this.nickname = nickname;
        this.domesticUniv = domesticUniv;
    }

    // 닉네임 업데이트 메서드
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }
}
