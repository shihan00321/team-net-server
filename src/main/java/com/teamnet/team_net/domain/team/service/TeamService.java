package com.teamnet.team_net.domain.team.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.team.controller.TeamRequest;
import com.teamnet.team_net.domain.team.dto.TeamResponse;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public Long createTeam(Long memberId, TeamRequest.CreateTeamDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Team team = Team.builder()
                .name(request.getName())
                .status(TeamActiveStatus.ACTIVE)
                .build();
        teamRepository.save(team);

        teamMemberRepository.save(TeamMember.builder()
                .team(team)
                .member(member)
                .role(TeamRole.ADMIN)
                .build());

        return team.getId();
    }

    public List<TeamResponse.TeamResponseDto> findMyTeams(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Team> myTeam = teamMemberRepository.findMyTeam(memberId, TeamActiveStatus.ACTIVE);
        return myTeam.stream().map(team -> TeamResponse.TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .teamImage(null)
                .createdAt(team.getCreatedAt())
                .build()).collect(Collectors.toList());
    }
}
