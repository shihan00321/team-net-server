package com.teamnet.team_net.domain.team.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.notification.service.NotificationService;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.mapper.PostMapper;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.teamnet.team_net.domain.team.mapper.TeamMapper.*;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;
    private final EntityChecker entityChecker;

    @Transactional
    public TeamResponse.TeamResponseDto createTeam(Long memberId, TeamServiceDTO.CreateTeamServiceDTO request) {
        Member member = entityChecker.findMemberById(memberId);
        Team team = toTeam(request);
        teamRepository.save(team);

        TeamMember admin = TeamMember.createAdmin(team, member);
        teamMemberRepository.save(admin);

        return toTeamResponseDto(team);
    }

    public TeamResponse.TeamListResponseDto findMyTeams(Long memberId) {
        entityChecker.findMemberById(memberId);
        List<Team> myTeam = teamMemberRepository.findTeamsByMemberIdAndStatus(memberId, TeamActiveStatus.ACTIVE);
        return toTeamListResponseDto(myTeam);
    }

    @Transactional
    public void invite(Long memberId, Long teamId, TeamServiceDTO.InviteMemberServiceDTO inviteMemberDto) {
        Member targetMember = entityChecker.findMemberByEmail(inviteMemberDto.getEmail());
        TeamMember admin = entityChecker.findTeamMemberByMemberIdAndTeamIdAndRole(memberId, teamId, TeamRole.ADMIN);
        notificationService.sendTeamInvitation(admin.getMember(), targetMember, teamId);
    }

    @Transactional
    public void accept(Long memberId, Long teamId) {
        Member member = entityChecker.findMemberById(memberId);
        Team team = entityChecker.findTeamById(teamId);

        TeamMember teamMember = TeamMember.createMember(team, member);
        teamMemberRepository.save(teamMember);
    }

    public PostResponse.PostListResponseDto findTeamPosts(Long memberId, Long teamId) {
        entityChecker.findTeamMemberByMemberIdAndTeamId(memberId, teamId);
        List<Post> teamPosts = postRepository.findAllByTeamId(teamId);
        return PostMapper.toPostListResponseDto(teamPosts);
    }

    @Transactional
    public void deleteTeam(Long memberId, Long teamId) {
        TeamMember member = entityChecker.findTeamMemberByMemberIdAndTeamIdAndRole(memberId, teamId, TeamRole.ADMIN);
        Team team = member.getTeam();
        team.delete();
    }
}
