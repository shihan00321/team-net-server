package com.teamnet.team_net.domain.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.team.controller.TeamController;
import com.teamnet.team_net.domain.team.controller.TeamRequest;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
            TeamServiceDTO.CreateTeamServiceDTO request = createTeamRequest("team");
            TeamResponse.TeamResponseDto response = createTeamResponse(DEFAULT_TEAM_ID, request.getName());
            given(teamService.createTeam(eq(DEFAULT_MEMBER_ID), any(TeamServiceDTO.CreateTeamServiceDTO.class)))
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
            TeamServiceDTO.CreateTeamServiceDTO request = createTeamRequest("");

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
            PageRequest pageRequest = PageRequest.of(0, 10);
            TeamResponse.TeamListResponseDto teamResponses = createTeamResponses(pageRequest);
            when(teamService.findMyTeams(eq(DEFAULT_MEMBER_ID), any(Pageable.class))).thenReturn(teamResponses);

            performGet(BASE_URL)
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.teams.content.size()").value(2),
                            jsonPath("$.result.teams.content[1].id").value(2L),
                            jsonPath("$.result.teams.content[1].name").value("team2")
                    );
        }

    }

    @Nested
    @DisplayName("팀원 관리 테스트")
    class TeamMemberTest {
//        @Test
//        @DisplayName("팀원을 초대한다")
//        @WithMockUser(roles = "USER")
//        void inviteMember() throws Exception {
//            // Given
//            TeamServiceDTO.InviteMemberServiceDTO request = createInviteRequest(DEFAULT_EMAIL);
//            doNothing().when(teamService)
//                    .invite(eq(DEFAULT_MEMBER_ID), eq(DEFAULT_TEAM_ID), any(TeamServiceDTO.InviteMemberServiceDTO.class));
//
//            // When & Then
//            performPost(teamUrl("/invite"), request)
//                    .andExpectAll(
//                            status().isOk(),
//                            jsonPath("$.isSuccess").value(true)
//                    );
//        }

        @Test
        @DisplayName("팀원 초대 성공")
        @WithMockUser(roles = "USER")
        void inviteMember() throws Exception {
            // Given
            TeamRequest.InviteMemberDTO request = TeamRequest.InviteMemberDTO.builder()
                    .email(DEFAULT_EMAIL)
                    .build();

            doNothing().when(teamService)
                    .invite(anyLong(), anyLong(), any(TeamServiceDTO.InviteMemberServiceDTO.class));

            // When & Then
            performPost("/api/teams/1/invite", request)
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

    private TeamServiceDTO.CreateTeamServiceDTO createTeamRequest(String name) {
        return TeamServiceDTO.CreateTeamServiceDTO.builder()
                .name(name)
                .build();
    }

    private TeamServiceDTO.InviteMemberServiceDTO createInviteRequest(String email) {
        return TeamServiceDTO.InviteMemberServiceDTO.builder()
                .email(email)
                .build();
    }

    private TeamResponse.TeamListResponseDto createTeamResponses(PageRequest pageRequest) {
        List<TeamResponse.TeamResponseDto> teamDtos = List.of(
                createTeamResponse(1L, "team1"),
                createTeamResponse(2L, "team2")
        );
        PageImpl<TeamResponse.TeamResponseDto> pageResult = new PageImpl<>(
                teamDtos,
                pageRequest,
                10 // 전체 요소 개수
        );
        return TeamResponse.TeamListResponseDto.builder()
                .teams(pageResult)
                .build();
    }

    private TeamResponse.TeamResponseDto createTeamResponse(Long id, String name) {
        return TeamResponse.TeamResponseDto.builder().id(id).name(name).createdAt(LocalDateTime.MIN).build();
    }

    private PostResponse.PostListResponseDto createPostResponses(PageRequest pageRequest) {
        List<PostResponse.PostResponseDto> dtos = List.of(
                PostResponse.PostResponseDto.builder().id(1L)
                        .title("제목1").content("내용1").build(),
                PostResponse.PostResponseDto.builder().id(2L)
                        .title("제목2").content("내용2").build());
        PageImpl<PostResponse.PostResponseDto> pageResult = new PageImpl<PostResponse.PostResponseDto>(
                dtos,
                pageRequest,
                15 // 전체 요소 개수
        );
        return PostResponse.PostListResponseDto.builder()
                .posts(pageResult)
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