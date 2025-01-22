package com.teamnet.team_net.domain.team;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

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
            TeamServiceDTO.CreateTeamServiceDTO request = createTeamDto(DEFAULT_TEAM_NAME);

            // When
            TeamResponse.TeamResponseDto teamResponseDto = teamService.createTeam(defaultMember.getId(), request);

            // Then
            Team savedTeam = teamRepository.findById(teamResponseDto.getId()).get();
            assertThat(teamResponseDto.getName()).isEqualTo(savedTeam.getName());
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
            PageRequest pageRequest = PageRequest.of(0, 20);

            // When
            TeamResponse.TeamListResponseDto response = teamService.findMyTeams(defaultMember.getId(), pageRequest);

            // Then
            assertThat(response.getTeams())
                    .hasSize(2)
                    .allSatisfy(team -> {
                        assertThat(team.getName()).isNotNull();
                        assertThat(team.getCreatedAt()).isNotNull();
                    });
        }

        @Test
        @DisplayName("존재하지 않는 회원의 팀 목록 조회시 예외가 발생한다")
        void findMyTeams_memberNotFound() {
            PageRequest pageRequest = PageRequest.of(0, 20);
            assertThatThrownBy(() -> teamService.findMyTeams(999L, pageRequest))
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
            TeamServiceDTO.InviteMemberServiceDTO inviteDto = createInviteDto(targetMember.getEmail());

            // When & Then
            assertDoesNotThrow(() ->
                    teamService.invite(defaultMember.getId(), defaultTeam.getId(), inviteDto));
        }

        @Test
        @DisplayName("권한이 없는 멤버의 초대시 예외가 발생한다")
        void invite_unauthorizedMember() {
            // Given
            Member normalMember = createMember("normal@test.com", "normal");
            TeamServiceDTO.InviteMemberServiceDTO inviteDto = createInviteDto(defaultMember.getEmail());

            // When & Then
            assertThatThrownBy(() ->
                    teamService.invite(normalMember.getId(), defaultTeam.getId(), inviteDto))
                    .isInstanceOf(TeamHandler.class);
        }
    }

    // Utility Methods
    private TeamServiceDTO.CreateTeamServiceDTO createTeamDto(String name) {
        return TeamServiceDTO.CreateTeamServiceDTO.builder()
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

    private TeamServiceDTO.InviteMemberServiceDTO createInviteDto(String email) {
        return TeamServiceDTO.InviteMemberServiceDTO.builder()
                .email(email)
                .build();
    }
}