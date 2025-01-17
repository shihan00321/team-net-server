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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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

    private static final String DEFAULT_EMAIL = "xxx@xxx.com";
    private static final String DEFAULT_NICKNAME = "default";
    private static final String DEFAULT_TEAM_NAME = "team";
    private static final String DEFAULT_NAME = "name";

    private Member defaultMember;
    private Team defaultTeam;

    @BeforeEach
    void setUp() {
        defaultMember = createMember(DEFAULT_EMAIL, DEFAULT_NICKNAME);
        defaultTeam = createTeam(DEFAULT_TEAM_NAME);
    }

    @Nested
    @DisplayName("팀 생성 테스트")
    class CreateTeam {
        @Test
        @DisplayName("성공적으로 팀을 생성한다")
        void createTeam_success() {
            // Given
            TeamRequest.CreateTeamDto request = createTeamDto(DEFAULT_TEAM_NAME);

            // When
            Long teamId = teamService.createTeam(defaultMember.getId(), request);

            // Then
            assertTeam(teamId);
        }
    }

    @Nested
    @DisplayName("팀 조회 테스트")
    class FindTeam {
        @Test
        @DisplayName("내 팀 목록을 성공적으로 조회한다")
        void findMyTeams_success() {
            // Given
            Team secondTeam = createTeam("Second Team");
            createTeamMember(defaultMember, defaultTeam, TeamRole.ADMIN);
            createTeamMember(defaultMember, secondTeam, TeamRole.MEMBER);

            // When
            List<TeamResponse.TeamResponseDto> teams = teamService.findMyTeams(defaultMember.getId());

            // Then
            assertThat(teams)
                    .hasSize(2)
                    .allSatisfy(team -> {
                        assertThat(team.getName()).isNotNull();
                        assertThat(team.getCreatedAt()).isNotNull();
                    });
        }

        @Test
        @DisplayName("존재하지 않는 회원의 팀 목록 조회시 예외가 발생한다")
        void findMyTeams_memberNotFound() {
            assertThatThrownBy(() -> teamService.findMyTeams(999L))
                    .isInstanceOf(MemberHandler.class);
        }
    }

    @Nested
    @DisplayName("팀원 초대 테스트")
    class InviteMember {
        @Test
        @DisplayName("관리자가 성공적으로 팀원을 초대한다")
        void invite_success() {
            // Given
            Member targetMember = createMember("target@test.com", "target");
            createTeamMember(defaultMember, defaultTeam, TeamRole.ADMIN);
            TeamRequest.InviteMemberDto inviteDto = createInviteDto(targetMember.getEmail());

            // When & Then
            assertDoesNotThrow(() ->
                    teamService.invite(defaultMember.getId(), defaultTeam.getId(), inviteDto));
        }

        @Test
        @DisplayName("권한이 없는 멤버의 초대시 예외가 발생한다")
        void invite_unauthorizedMember() {
            // Given
            Member normalMember = createMember("normal@test.com", "normal");
            TeamRequest.InviteMemberDto inviteDto = createInviteDto(defaultMember.getEmail());

            // When & Then
            assertThatThrownBy(() ->
                    teamService.invite(normalMember.getId(), defaultTeam.getId(), inviteDto))
                    .isInstanceOf(TeamHandler.class);
        }
    }

    @Nested
    @DisplayName("팀 게시글 테스트")
    class TeamPosts {
        @Test
        @DisplayName("팀 게시글 목록을 성공적으로 조회한다")
        void findTeamPosts_success() {
            // Given
            createTeamMember(defaultMember, defaultTeam, TeamRole.ADMIN);
            createPost(defaultTeam, defaultMember, "제목1", "내용1");
            createPost(defaultTeam, defaultMember, "제목2", "내용2");

            // When
            List<PostResponse.PostResponseDto> posts =
                    teamService.findTeamPosts(defaultMember.getId(), defaultTeam.getId());

            // Then
            assertThat(posts)
                    .hasSize(2)
                    .satisfies(postList -> {
                        assertThat(postList.get(0))
                                .satisfies(post -> {
                                    assertThat(post.getTitle()).isEqualTo("제목1");
                                    assertThat(post.getContent()).isEqualTo("내용1");
                                });
                    });
        }
    }

    // Utility Methods
    private TeamRequest.CreateTeamDto createTeamDto(String name) {
        return TeamRequest.CreateTeamDto.builder()
                .name(name)
                .build();
    }

    private Member createMember(String email, String nickname) {
        return memberRepository.save(Member.builder()
                .name(DEFAULT_NAME)
                .status(DeletionStatus.NOT_DELETE)
                .role(Role.USER)
                .email(email)
                .nickname(nickname)
                .build());
    }

    private Team createTeam(String name) {
        return teamRepository.save(Team.builder()
                .status(TeamActiveStatus.ACTIVE)
                .name(name)
                .build());
    }

    private TeamMember createTeamMember(Member member, Team team, TeamRole role) {
        return teamMemberRepository.save(TeamMember.builder()
                .role(role)
                .member(member)
                .team(team)
                .build());
    }

    private Post createPost(Team team, Member member, String title, String content) {
        return postRepository.save(Post.builder()
                .title(title)
                .content(content)
                .member(member)
                .team(team)
                .build());
    }

    private TeamRequest.InviteMemberDto createInviteDto(String email) {
        return TeamRequest.InviteMemberDto.builder()
                .email(email)
                .build();
    }

    private void assertTeam(Long teamId) {
        Team savedTeam = teamRepository.findById(teamId).get();
        assertThat(teamId).isEqualTo(savedTeam.getId());
    }
}