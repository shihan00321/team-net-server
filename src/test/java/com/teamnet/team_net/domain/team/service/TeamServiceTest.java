package com.teamnet.team_net.domain.team.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamMemberRepository teamMemberRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("팀 생성 기능 테스트")
    void createTeam() {
        Member savedMember = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("xxx.xxx.com")
                .nickname("hbb")
                .build());

        TeamRequest.CreateTeamDto request = TeamRequest.CreateTeamDto.builder()
                .name("team")
                .build();

        Long teamId = teamService.createTeam(savedMember.getId(), request);

        Team savedTeam = teamRepository.findById(teamId).get();
        assertThat(teamId).isEqualTo(savedTeam.getId());
    }

    @Test
    @DisplayName("내 팀 목록 조회 성공")
    void findMyTeams_success() {
        // Given
        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());

        Team savedTeam2 = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team2")
                .build());

        Member savedMember = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("xxx.xxx.com")
                .nickname("hbb")
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .role(TeamRole.ADMIN)
                .team(savedTeam)
                .member(savedMember)
                .build());


        teamMemberRepository.save(TeamMember.builder()
                .role(TeamRole.MEMBER)
                .team(savedTeam2)
                .member(savedMember)
                .build());

        // When
        List<TeamResponse.TeamResponseDto> teams = teamService.findMyTeams(savedMember.getId());

        // Then
        assertThat(teams).isNotEmpty();
        assertThat(teams).hasSize(2);
        assertThat(teams.get(0).getName()).isNotNull();
        assertThat(teams.get(0).getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 회원의 팀 목록 조회 실패")
    void findMyTeams_memberNotFound() {
        // Given
        Long nonExistentMemberId = 999L;

        // When & Then
        assertThrows(MemberHandler.class, () -> teamService.findMyTeams(nonExistentMemberId));
    }

    @Test
    @DisplayName("팀원 초대 성공")
    void invite_success() {
        // Given
        Member memberA = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());

        Member memberB = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("bbb.bbb.com")
                .nickname("bbb")
                .build());

        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());

        teamMemberRepository.save(TeamMember.builder()
                        .member(memberA)
                        .team(savedTeam)
                        .role(TeamRole.ADMIN)
                .build());


        TeamRequest.InviteMemberDto inviteDto = TeamRequest.InviteMemberDto.builder()
                .email("bbb.bbb.com")
                .build();
        // 예외 발생 x
        teamService.invite(memberA.getId(), savedTeam.getId(), inviteDto);
    }


    @Test
    @DisplayName("관리자 권한이 없는 멤버의 초대 실패")
    void invite_unauthorizedMember() {

        // Given
        Member memberA = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());

        Member memberB = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("bbb.bbb.com")
                .nickname("bbb")
                .build());

        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());


        TeamRequest.InviteMemberDto inviteDto = TeamRequest.InviteMemberDto.builder()
                .email("aaa.aaa.com")
                .build();

        assertThrows(TeamHandler.class, () ->
                teamService.invite(memberB.getId(), savedTeam.getId(), inviteDto));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 초대 실패")
    void invite_targetMemberNotFound() {
        // Given
        Member memberA = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());


        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());


        TeamRequest.InviteMemberDto inviteDto = TeamRequest.InviteMemberDto.builder()
                .email("ccc.ccc.com")
                .build();

        // When & Then
        assertThrows(MemberHandler.class, () ->
                teamService.invite(memberA.getId(), savedTeam.getId(), inviteDto));
    }

    @Test
    @DisplayName("팀 초대 수락 성공")
    void accept_success() {
        // Given
        Member member = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());

        Member newMember = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("bbb.bbb.com")
                .nickname("bbb")
                .build());


        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .role(TeamRole.MEMBER)
                .member(member)
                .team(savedTeam)
                .build());

        // When
        teamService.accept(newMember.getId(), savedTeam.getId());

        // Then
        Optional<TeamMember> savedTeamMember = teamMemberRepository.findByMemberIdAndTeamId(newMember.getId(), savedTeam.getId());
        assertThat(savedTeamMember).isPresent();
        assertThat(savedTeamMember.get().getRole()).isEqualTo(TeamRole.MEMBER);
        assertThat(savedTeamMember.get().getMember().getId()).isEqualTo(newMember.getId());
        assertThat(savedTeamMember.get().getTeam().getId()).isEqualTo(savedTeam.getId());
    }

    @Test
    @DisplayName("팀 게시글 목록 조회 성공")
    void findTeamPosts_success() {

        Member member = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());

        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());

        postRepository.save(Post.builder()
                .title("제목1")
                .content("내용1")
                .member(member)
                .team(savedTeam)
                .build());

        postRepository.save(Post.builder()
                .title("제목2")
                .content("내용2")
                .member(member)
                .team(savedTeam)
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .role(TeamRole.ADMIN)
                .member(member)
                .team(savedTeam)
                .build());

        List<PostResponse.PostResponseDto> posts = teamService.findTeamPosts(member.getId(), savedTeam.getId());

        assertThat(posts).hasSize(2);
        assertThat(posts.get(0).getTitle()).isEqualTo("제목1");
        assertThat(posts.get(0).getContent()).isEqualTo("내용1");
    }

    @Test
    @DisplayName("관리자의 팀 삭제 성공")
    void deleteTeam_success() {
        Member member = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());

        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .role(TeamRole.ADMIN)
                .member(member)
                .team(savedTeam)
                .build());

        Long deletedTeamId = teamService.deleteTeam(member.getId(), savedTeam.getId());

        Team deletedTeam = teamRepository.findById(deletedTeamId).orElseThrow();
        assertThat(deletedTeam.getStatus()).isEqualTo(TeamActiveStatus.INACTIVE);
        assertThat(deletedTeamId).isEqualTo(savedTeam.getId());
    }

    @Test
    @DisplayName("일반 멤버의 팀 삭제 실패")
    void deleteTeam_unauthorizedMember() {
        Member member = memberRepository.save(Member.builder()
                .name("name")
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email("aaa.aaa.com")
                .nickname("aaa")
                .build());

        Team savedTeam = teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name("team")
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .role(TeamRole.MEMBER)
                .member(member)
                .team(savedTeam)
                .build());

        assertThrows(TeamHandler.class, () -> teamService.deleteTeam(member.getId(), savedTeam.getId()));
    }
}