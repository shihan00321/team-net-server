package com.teamnet.team_net.domain.team.entity;

import com.teamnet.team_net.global.common.entity.BaseTimeEntity;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Team extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamActiveStatus status;
}
