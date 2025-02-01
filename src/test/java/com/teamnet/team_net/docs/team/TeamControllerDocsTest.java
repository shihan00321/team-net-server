package com.teamnet.team_net.docs.team;

import com.teamnet.team_net.docs.RestDocsSupport;
import com.teamnet.team_net.domain.team.controller.TeamController;
import com.teamnet.team_net.domain.team.controller.TeamRequest;
import com.teamnet.team_net.domain.team.enums.TeamSearchType;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TeamControllerDocsTest extends RestDocsSupport {

    private static final String BASE_URL = "/api/teams";
    private static final Long DEFAULT_TEAM_ID = 1L;
    private static final Long DEFAULT_MEMBER_ID = 1L;
    private static final String DEFAULT_EMAIL = "xxx.xxx.com";

    TeamService teamService = mock(TeamService.class);

    @Override
    protected Object initController() {
        return new TeamController(teamService);
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
            TeamResponse.TeamResponseDto response = createTeamResponse(DEFAULT_TEAM_ID, "hbb", request.getName());
            when(teamService.createTeam(eq(DEFAULT_MEMBER_ID), any(TeamServiceDTO.CreateTeamServiceDTO.class)))
                    .thenReturn(response);

            // When & Then
            mvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(mockHttpSession)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.name").value(response.getName()))
                    .andDo(document("team-create",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("팀 이름 (중복될 수 없다.)")
                            ),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result.id").type(JsonFieldType.NUMBER)
                                            .description("게시글 ID"),
                                    fieldWithPath("result.name").type(JsonFieldType.STRING)
                                            .description("팀 이름"),
                                    fieldWithPath("result.teamImage").type(JsonFieldType.STRING)
                                            .description("팀 대표 이미지"),
                                    fieldWithPath("result.createdBy").type(JsonFieldType.STRING)
                                            .description("팀장 이름"),
                                    fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                            .description("팀 생성 시간")
                            )));

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
                            .session(mockHttpSession))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.teams.content.size()").value(2),
                            jsonPath("$.result.teams.content[1].id").value(2L),
                            jsonPath("$.result.teams.content[1].name").value("team2")
                    ).andDo(document("team-find-all",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result.teams.content").type(JsonFieldType.ARRAY)
                                            .description("나의 팀 목록").optional(),
                                    fieldWithPath("result.teams.content[].id").type(JsonFieldType.NUMBER)
                                            .description("팀 ID"),
                                    fieldWithPath("result.teams.content[].name").type(JsonFieldType.STRING)
                                            .description("팀 이름"),
                                    fieldWithPath("result.teams.content[].teamImage").type(JsonFieldType.STRING)
                                            .description("팀 대표 이미지"),
                                    fieldWithPath("result.teams.content[].createdBy").type(JsonFieldType.STRING)
                                            .description("팀장 이름"),
                                    fieldWithPath("result.teams.content[].createdAt").type(JsonFieldType.STRING)
                                            .description("팀 생성 시간"),
                                    fieldWithPath("result.teams.page.size").type(JsonFieldType.NUMBER)
                                            .description("페이지 크기"),
                                    fieldWithPath("result.teams.page.number").type(JsonFieldType.NUMBER)
                                            .description("현재 페이지 번호"),
                                    fieldWithPath("result.teams.page.totalPages").type(JsonFieldType.NUMBER)
                                            .description("총 페이지 수"),
                                    fieldWithPath("result.teams.page.totalElements").type(JsonFieldType.NUMBER)
                                            .description("총 게시글 수")
                            )));
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
                            .session(mockHttpSession)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true)
                    ).andDo(document("team-invite",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지")
                            )));
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
                            .session(mockHttpSession))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").value("Invitation accepted")
                    ).andDo(document("team-accept",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과")
                            )));
        }

        @Test
        @DisplayName("초대 요청을 거절한다.")
        @WithMockUser(roles = "USER")
        void rejectInvitation() throws Exception {
            mvc.perform(post(BASE_URL + "/{teamId}/reject", DEFAULT_TEAM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(mockHttpSession))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result").value("Invitation rejected")
                    ).andDo(document("team-inject",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result").type(JsonFieldType.STRING).description("결과")
                            )));
        }
    }


    @Nested
    @DisplayName("팀 검색 시 조건에 맞는 팀이 조회된다.")
    class TeamSearchTest {
        @Test
        @DisplayName("팀 이름으로 검색 시 해당 조건에 맞는 팀이 조회된다.")
        @WithMockUser(roles = "USER")
        void searchTeamByTeamName() throws Exception {
            // given
            TeamResponse.TeamResponseDto response = createTeamResponse(DEFAULT_TEAM_ID, "hbb", "team");

            // when & then
            when(teamService.searchTeam(any(TeamServiceDTO.TeamSearchServiceDTO.class)))
                    .thenReturn(response);

            mvc.perform(get(BASE_URL + "/search")
                            .session(mockHttpSession)
                            .param("keyword", "team")
                            .param("type", TeamSearchType.NAME.name()))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.name").value("team")
                    ).andDo(document("team-search",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("keyword").description("검색 키워드"),
                                    parameterWithName("type").description("검색 타입 (NAME, AUTHOR)")
                            ),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result").type(JsonFieldType.OBJECT).description("검색 조건에 부합하는 팀 반환").optional(),
                                    fieldWithPath("result.id").type(JsonFieldType.NUMBER).description("팀 ID"),
                                    fieldWithPath("result.name").type(JsonFieldType.STRING).description("결과"),
                                    fieldWithPath("result.teamImage").type(JsonFieldType.STRING).description("팀 대표 이미지 URL"),
                                    fieldWithPath("result.createdBy").type(JsonFieldType.STRING).description("팀장 이름"),
                                    fieldWithPath("result.createdAt").type(JsonFieldType.STRING).description("팀 생성 일자")
                            )));
        }
    }

    private TeamRequest.CreateTeamDTO createTeamRequest(String name) {
        return TeamRequest.CreateTeamDTO.builder()
                .name(name)
                .build();
    }

    private TeamResponse.TeamListResponseDto createTeamResponses(PageRequest pageRequest) {
        List<TeamResponse.TeamResponseDto> teamDtos = List.of(
                createTeamResponse(1L, "hbb", "team1"),
                createTeamResponse(2L, "hbb", "team2")
        );
        PageImpl<TeamResponse.TeamResponseDto> pageResult = new PageImpl<>(
                teamDtos,
                pageRequest,
                10
        );

        PagedModel<TeamResponse.TeamResponseDto> pagedModel = new PagedModel<>(pageResult);
        return TeamResponse.TeamListResponseDto.builder()
                .teams(pagedModel)
                .build();
    }

    private TeamResponse.TeamResponseDto createTeamResponse(Long id, String createdBy, String name) {
        return TeamResponse.TeamResponseDto.builder()
                .id(id)
                .name(name)
                .teamImage("ImageUrl")
                .createdBy(createdBy)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
