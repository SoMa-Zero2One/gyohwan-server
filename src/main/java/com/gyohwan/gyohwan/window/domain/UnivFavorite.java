package com.gyohwan.gyohwan.window.domain;

import com.gyohwan.gyohwan.common.domain.BaseEntity;
import com.gyohwan.gyohwan.common.domain.User;
import com.gyohwan.gyohwan.compare.domain.OutgoingUniv;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "univ_favorite",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "univ_favorite_uk",
                        columnNames = {"user_id", "outgoing_univ_id"}
                )
        }
)
public class UnivFavorite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outgoing_univ_id", nullable = false)
    private OutgoingUniv outgoingUniv;

    public UnivFavorite(User user, OutgoingUniv outgoingUniv) {
        this.user = user;
        this.outgoingUniv = outgoingUniv;
    }
}

