package com.teamnet.team_net.domain.team;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    @DisplayName("새로운 팀을 생성한다.")
    class CreateTeamTest {
        @Test
        @DisplayName("성공적으로 팀을 생성한다")
        @WithMockUser(roles = "USER")
        void success() throws Exception {
            // Given
            TeamRequest.CreateTeamDTO request = createTeamRequest("team");
            TeamResponse.TeamResponseDto response = createTeamResponse(DEFAULT_TEAM_ID, request.getName());
            when(teamService.createTeam(eq(DEFAULT_MEMBER_ID), any(TeamServiceDTO.CreateTeamServiceDTO.class)))
                    .thenReturn(response);

            // When & Then
            mvc.perform(
                    post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf())
            ).andExpectAll(
                    status().isOk(),
                    jsonPath("$.isSuccess").value(true),
                    jsonPath("$.result.name").value(response.getName()));

        }


        @Test
        @DisplayName("신규 팀 생성 시 팀 이름은 필수이다.")
        @WithMockUser(roles = "USER")
        void validationFailure() throws Exception {
            // Given
            TeamRequest.CreateTeamDTO request = createTeamRequest("");

            // When & Then
            mvc.perform(
                    post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf())
            ).andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.isSuccess").value(false),
                    jsonPath("$.message").value("팀 이름은 비어있을 수 없습니다."));
        }
    }

    @Nested
    @DisplayName("나의 팀 목록을 조회한다.")
    class FindTeamTest {

        @Test
        @DisplayName("나의 팀 목록을 조회한다")
        @WithMockUser(roles = "USER")
        void findMyTeams() throws Exception {
            // Given
            PageRequest pageRequest = PageRequest.of(0, 10);
            TeamResponse.TeamListResponseDto teamResponses = createTeamResponses(pageRequest);
            when(teamService.findMyTeams(eq(DEFAULT_MEMBER_ID), any(Pageable.class))).thenReturn(teamResponses);

            mvc.perform(get(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .with(csrf()))
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
    @DisplayName("팀원 초대 및 거절 상태를 관리한다.")
    class TeamMemberTest {
        @Test
        @DisplayName("이메일을 통하여 팀원을 찾고 초대한다.")
        @WithMockUser(roles = "USER")
        void inviteMember() throws Exception {
            // Given
            TeamRequest.InviteMemberDTO request = TeamRequest.InviteMemberDTO.builder()
                    .email(DEFAULT_EMAIL)
                    .build();

            doNothing().when(teamService)
                    .invite(anyLong(), anyLong(), any(TeamServiceDTO.InviteMemberServiceDTO.class));

            // When & Then
            mvc.perform(post("/api/teams/{teamId}/invite", DEFAULT_TEAM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true)
                    ).andDo(print());
        }

        @Test
        @DisplayName("초대 요청을 수락한다.")
        @WithMockUser(roles = "USER")
        void acceptInvitation() throws Exception {
            // Given
            doNothing().when(teamService)
                    .accept(DEFAULT_MEMBER_ID, DEFAULT_TEAM_ID);

            // When & Then
            mvc.perform(post(BASE_URL + "/{teamId}/accept", DEFAULT_TEAM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .with(csrf()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").value("Invitation accepted")
                    );
        }

        @Test
        @DisplayName("초대 요청을 거절한다.")
        @WithMockUser(roles = "USER")
        void rejectInvitation() throws Exception {
            mvc.perform(post(BASE_URL + "/{teamId}/reject", DEFAULT_TEAM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(session)
                            .with(csrf()))
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

    private TeamRequest.CreateTeamDTO createTeamRequest(String name) {
        return TeamRequest.CreateTeamDTO.builder()
                .name(name)
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
                10
        );
        return TeamResponse.TeamListResponseDto.builder()
                .teams(pageResult)
                .build();
    }

    private TeamResponse.TeamResponseDto createTeamResponse(Long id, String name) {
        return TeamResponse.TeamResponseDto.builder().id(id).name(name).createdAt(LocalDateTime.MIN).build();
    }
}