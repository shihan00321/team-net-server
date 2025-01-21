package com.teamnet.team_net.domain.teammember.entity;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class TeamMember extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "team_member_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;


    public static TeamMember createAdmin(Team team, Member member) {
        return TeamMember.builder()
                .team(team)
                .member(member)
                .role(TeamRole.ADMIN)
                .build();
    }

    public static TeamMember createMember(Team team, Member member) {
        return TeamMember.builder()
                .team(team)
                .member(member)
                .role(TeamRole.MEMBER)
                .build();
    }
}
