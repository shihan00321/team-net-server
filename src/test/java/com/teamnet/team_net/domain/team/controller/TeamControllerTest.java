package com.teamnet.team_net.domain.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.team.dto.TeamResponse;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.global.config.SecurityConfig;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class TeamControllerTest {
    private static final String BASE_URL = "/api/teams";
    private static final Long DEFAULT_TEAM_ID = 1L;
    private static final Long DEFAULT_MEMBER_ID = 1L;
    private static final String DEFAULT_EMAIL = "xxx.xxx.com";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TeamService teamService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession session;

    @BeforeEach
    void setUp() {
        SessionMember sessionMember = createDefaultSessionMember();
        session = new MockHttpSession();
        session.setAttribute("member", sessionMember);
    }

    @Nested
    @DisplayName("팀 생성 테스트")
    class CreateTeamTest {
        @Test
        @DisplayName("성공적으로 팀을 생성한다")
        @WithMockUser(roles = "USER")
        void success() throws Exception {
            // Given
            TeamRequest.CreateTeamDto request = createTeamRequest("team");
            TeamResponse.TeamResponseDto response = createTeamResponse(DEFAULT_TEAM_ID, request.getName());
            given(teamService.createTeam(eq(DEFAULT_MEMBER_ID), any(TeamRequest.CreateTeamDto.class)))
                    .willReturn(response);

            // When & Then
            performPost(BASE_URL, request)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.name").value(response.getName())
                    );
        }

        @Test
        @DisplayName("유효하지 않은 팀 이름으로 생성 실패")
        @WithMockUser(roles = "USER")
        void validationFailure() throws Exception {
            // Given
            TeamRequest.CreateTeamDto request = createTeamRequest("");

            // When & Then
            performPost(BASE_URL, request)
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("팀 조회 테스트")
    class FindTeamTest {
        @Test
        @DisplayName("내 팀 목록을 조회한다")
        @WithMockUser(roles = "USER")
        void findMyTeams() throws Exception {
            // Given
            TeamResponse.TeamListResponseDto teamResponses = createTeamResponses();
            when(teamService.findMyTeams(DEFAULT_MEMBER_ID)).thenReturn(teamResponses);

            // When & Then
            performGet(BASE_URL)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.teams.size()").value(2),
                            jsonPath("$.result.teams[1].id").value(2L),
                            jsonPath("$.result.teams[1].name").value("team2")
                    );
        }

        @Test
        @DisplayName("팀별 게시글을 조회한다")
        @WithMockUser(roles = "USER")
        void findTeamPosts() throws Exception {
            // Given
            PostResponse.PostListResponseDto postResponses = createPostResponses();
            when(teamService.findTeamPosts(DEFAULT_MEMBER_ID, DEFAULT_TEAM_ID))
                    .thenReturn(postResponses);

            // When & Then
            performGet(teamUrl(DEFAULT_TEAM_ID))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.posts.size()").value(2),
                            jsonPath("$.result.posts[1].id").value(2L),
                            jsonPath("$.result.posts[1].title").value("제목2"),
                            jsonPath("$.result.posts[1].content").value("내용2")
                    );
        }
    }

    @Nested
    @DisplayName("팀원 관리 테스트")
    class TeamMemberTest {
        @Test
        @DisplayName("팀원을 초대한다")
        @WithMockUser(roles = "USER")
        void inviteMember() throws Exception {
            // Given
            TeamRequest.InviteMemberDto request = createInviteRequest(DEFAULT_EMAIL);
            doNothing().when(teamService)
                    .invite(DEFAULT_MEMBER_ID, DEFAULT_TEAM_ID, request);

            // When & Then
            performPost(teamUrl("/invite"), request)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true)
                    );
        }

        @Test
        @DisplayName("초대를 수락한다")
        @WithMockUser(roles = "USER")
        void acceptInvitation() throws Exception {
            // Given
            doNothing().when(teamService)
                    .accept(DEFAULT_MEMBER_ID, DEFAULT_TEAM_ID);

            // When & Then
            performPost(teamUrl("/accept"), null)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").value("Invitation accepted")
                    );
        }

        @Test
        @DisplayName("초대를 거절한다")
        @WithMockUser(roles = "USER")
        void rejectInvitation() throws Exception {
            performPost(teamUrl("/reject"), null)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").value("Invitation rejected")
                    );
        }
    }

    // Utility Methods
    private SessionMember createDefaultSessionMember() {
        return new SessionMember(Member.builder()
                .id(DEFAULT_MEMBER_ID)
                .nickname("hbb")
                .build());
    }

    private TeamRequest.CreateTeamDto createTeamRequest(String name) {
        return TeamRequest.CreateTeamDto.builder()
                .name(name)
                .build();
    }

    private TeamRequest.InviteMemberDto createInviteRequest(String email) {
        return TeamRequest.InviteMemberDto.builder()
                .email(email)
                .build();
    }

    private TeamResponse.TeamListResponseDto createTeamResponses() {
        return TeamResponse.TeamListResponseDto.builder()
                .teams(List.of(
                        createTeamResponse(1L, "team1"),
                        createTeamResponse(2L, "team2")
                ))
                .build();
    }

    private TeamResponse.TeamResponseDto createTeamResponse(Long id, String name) {
        return TeamResponse.TeamResponseDto.builder().id(id).name(name).build();
    }

    private PostResponse.PostListResponseDto createPostResponses() {
        return PostResponse.PostListResponseDto.builder()
                .posts(List.of(
                        PostResponse.PostResponseDto.builder().id(1L)
                                .title("제목1").content("내용1").build(),
                        PostResponse.PostResponseDto.builder().id(2L)
                                .title("제목2").content("내용2").build()))
                .build();
    }

    private ResultActions performPost(String url, Object content) throws Exception {
        MockHttpServletRequestBuilder request = post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .with(csrf());

        if (content != null) {
            request.content(objectMapper.writeValueAsString(content));
        }

        return mvc.perform(request);
    }

    private ResultActions performGet(String url) throws Exception {
        return mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .with(csrf()));
    }

    private String teamUrl(Long teamId) {
        return BASE_URL + "/" + teamId;
    }

    private String teamUrl(String suffix) {
        return teamUrl(TeamControllerTest.DEFAULT_TEAM_ID) + suffix;
    }
}