package com.teamnet.team_net.domain.team.entity;

import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_id")
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamActiveStatus status;

    public void delete() {
        this.status = TeamActiveStatus.INACTIVE;
    }
}
