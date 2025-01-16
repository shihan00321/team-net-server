package com.teamnet.team_net.domain.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.global.config.SecurityConfig;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TeamController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class TeamControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TeamService teamService;

    @Mock
    private HttpSession httpSession;

    private SessionMember sessionMember;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        sessionMember = new SessionMember(Member.builder()
                .id(1L)
                .nickname("hbb")
                .build());
        when(httpSession.getAttribute("member")).thenReturn(sessionMember);
    }


    @Test
    @DisplayName("팀 생성 성공 테스트")
    @WithMockUser(roles = "USER")
    void createTeam() throws Exception {
        TeamRequest.CreateTeamDto teamRequestDto = TeamRequest.CreateTeamDto.builder()
                .name("team")
                .build();

        Long teamId = 1L;

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        given(teamService.createTeam(eq(sessionMember.getId()), any(TeamRequest.CreateTeamDto.class))).willReturn(teamId);

        mvc.perform(post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto))
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(teamId))
                .andDo(print());
    }

    @Test
    void myTeam() {
    }

    @Test
    void findTeamPosts() {
    }

    @Test
    void inviteMember() {
    }

    @Test
    void deleteTeam() {
    }

    @Test
    void acceptInvitation() {
    }

    @Test
    void rejectInvitation() {
    }
}