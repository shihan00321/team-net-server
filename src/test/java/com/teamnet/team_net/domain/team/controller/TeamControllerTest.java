package com.teamnet.team_net.domain.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.team.dto.TeamResponse;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    @DisplayName("내가 가입한 팀 리스트를 조회한다.")
    @WithMockUser(roles = "USER")
    void myTeam() throws Exception {
        List<TeamResponse.TeamResponseDto> teams = Arrays.asList(TeamResponse.TeamResponseDto.builder()
                .id(1L)
                .name("team1")
                .build(), TeamResponse.TeamResponseDto.builder()
                .id(2L)
                .name("team2")
                .build());

        when(teamService.findMyTeams(sessionMember.getId())).thenReturn(teams);
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        mvc.perform(get("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.size()").value(teams.size()))
                .andExpect(jsonPath("$.result[1].id").value(2L))
                .andExpect(jsonPath("$.result[1].name").value("team2"));
    }

    @Test
    @DisplayName("팀별 게시글 조회 기능 테스트")
    @WithMockUser(roles = "USER")
    void findTeamPosts() throws Exception {
        Long teamId = 1L;
        List<PostResponse.PostResponseDto> posts = Arrays.asList(
                PostResponse.PostResponseDto.builder()
                        .id(1L)
                        .title("제목1")
                        .content("내용1")
                        .build(), PostResponse.PostResponseDto.builder()
                        .id(2L)
                        .title("제목2")
                        .content("내용2")
                        .build());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        when(teamService.findTeamPosts(sessionMember.getId(), teamId)).thenReturn(posts);

        mvc.perform(get("/api/teams/{teamId}", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.size()").value(posts.size()))
                .andExpect(jsonPath("$.result[1].id").value(2L))
                .andExpect(jsonPath("$.result[1].title").value("제목2"))
                .andExpect(jsonPath("$.result[1].content").value("내용2"))
                .andDo(print());
    }

    @Test
    @DisplayName("팀 초대 기능 테스트")
    @WithMockUser(roles = "USER")
    void inviteMember() throws Exception {
        TeamRequest.InviteMemberDto request = TeamRequest.InviteMemberDto.builder()
                .email("xxx.xxx.com")
                .build();

        Long teamId = 1L;

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        doNothing().when(teamService).invite(sessionMember.getId(), teamId, request);

        mvc.perform(post("/api/teams/{teamId}/invite", teamId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .content(objectMapper.writeValueAsString(request))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("팀 삭제 테스트 기능 구현")
    @WithMockUser(roles = "USER")
    void deleteTeam() throws Exception {
        Long teamId = 1L;
        when(teamService.deleteTeam(sessionMember.getId(), teamId)).thenReturn(teamId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        mvc.perform(patch("/api/teams/{teamId}", teamId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    @DisplayName("팀 초대 요청 수락 기능 테스트")
    @WithMockUser(roles = "USER")
    void acceptInvitation() throws Exception {
        Long teamId = 1L;
        doNothing().when(teamService).accept(sessionMember.getId(), teamId);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);
        mvc.perform(post("/api/teams/{teamId}/accept", teamId)
                .contentType(MediaType.APPLICATION_JSON)
                .session(session)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Invitation accepted"));
    }

    @Test
    @DisplayName("팀 초대 요청 거절 기능 테스트")
    @WithMockUser(roles = "USER")
    void rejectInvitation() throws Exception {
        Long teamId = 1L;
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);
        mvc.perform(post("/api/teams/{teamId}/reject", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("Invitation rejected"));
    }
}