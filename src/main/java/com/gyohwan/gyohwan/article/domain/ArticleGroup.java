package com.gyohwan.gyohwan.article.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArticleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleScopeType scopeType;

    @Column(nullable = false, length = 20)
    private String targetCode;

    @OneToMany(mappedBy = "articleGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleConnection> articleConnects = new ArrayList<>();

}
