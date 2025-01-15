package com.teamnet.team_net.domain.team.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.notification.entity.Notification;
import com.teamnet.team_net.domain.notification.enums.NotificationType;
import com.teamnet.team_net.domain.notification.repository.NotificationRepository;
import com.teamnet.team_net.domain.notification.service.NotificationService;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.team.controller.TeamRequest;
import com.teamnet.team_net.domain.team.dto.TeamResponse;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.exception.handler.TeamHandler;
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
    private final PostRepository postRepository;
    private final NotificationService notificationService;

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

    @Transactional
    public void invite(Long memberId, Long teamId, TeamRequest.InviteMemberDto inviteMemberDto) {
        Member targetMember = memberRepository.findByEmail(inviteMemberDto.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Member member = teamMemberRepository.findMemberWithRole(memberId, TeamRole.ADMIN)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_INVITATION_UNAUTHORIZED));

        notificationService.send(member, targetMember, teamId);
    }

    @Transactional
    public void accept(Long memberId, Long teamId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_NOT_FOUND));

        teamMemberRepository.save(TeamMember.builder()
                .member(member)
                .team(team)
                .role(TeamRole.MEMBER)
                .build());
    }

    public List<PostResponse.PostResponseDto> findTeamPosts(Long memberId, Long teamId) {
        TeamMember teamMember = teamMemberRepository.findByMemberIdAndTeamId(memberId, teamId)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_NOT_FOUND));
        List<Post> teamPosts = postRepository.findAllByTeamId(teamId);
        return teamPosts.stream()
                .map(teamPost -> PostResponse.PostResponseDto.builder()
                        .id(teamPost.getId())
                        .title(teamPost.getTitle())
                        .content(teamPost.getContent())
                        .build()).collect(Collectors.toList());

    }
}
