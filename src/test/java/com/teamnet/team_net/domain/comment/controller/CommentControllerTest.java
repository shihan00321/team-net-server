package com.teamnet.team_net.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.comment.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.global.config.SecurityConfig;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {CommentController.class}, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class CommentControllerTest {

    private static final Long TEST_TEAM_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    private static final Long TEST_COMMENT_ID = 1L;
    private static final String BASE_URL = "/api/teams/{teamId}/posts/{postId}/comments";
    private static final String BASE_URL_WITH_COMMENT = BASE_URL + "/{commentId}";
    private static final String TEST_CONTENT = "테스트 댓글";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private CommentService commentService;

    @Autowired
    private ObjectMapper objectMapper;

    private MockHttpSession mockHttpSession;
    private SessionMember sessionMember;

    @BeforeEach
    void setUp() {
        sessionMember = new SessionMember(Member.builder()
                .id(1L)
                .nickname("hbb")
                .build());
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("member", sessionMember);
    }

    @Test
    @DisplayName("댓글 생성 기능 테스트")
    @WithMockUser(roles = "USER")
    void create_test() throws Exception {
        CommentRequest.CreateCommentDto request = createCommentDto(TEST_CONTENT, null);
        CommentResponse.CommentResponseDTO response = createCommentResponseDTO(request);

        when(commentService.createComment(eq(sessionMember.getId()), eq(TEST_TEAM_ID), eq(TEST_POST_ID), any(CommentRequest.CreateCommentDto.class)))
                .thenReturn(response);
        ResultActions actions = performRequest(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID), request);
        verifySuccessResponse(actions, response);
    }

    @Test
    @DisplayName("대댓글 생성 기능 테스트")
    @WithMockUser(roles = "USER")
    void create_in_comment_test() throws Exception {
        CommentRequest.CreateCommentDto request = createCommentDto(TEST_CONTENT, 1L);
        CommentResponse.CommentResponseDTO response = createCommentResponseDTO(request);
        when(commentService.createComment(eq(sessionMember.getId()), eq(TEST_TEAM_ID), eq(TEST_POST_ID), any(CommentRequest.CreateCommentDto.class)))
                .thenReturn(response);
        ResultActions actions = performRequest(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID), request);
        verifySuccessResponse(actions, response);
    }

    @Test
    @DisplayName("댓글 수정 기능 테스트")
    @WithMockUser(roles = "USER")
    void update_test() throws Exception {
        CommentRequest.CreateCommentDto request = createCommentDto("수정된 댓글", null);
        CommentResponse.CommentResponseDTO response = createCommentResponseDTO(request);
        when(commentService.updateComment(eq(sessionMember.getId()), eq(TEST_COMMENT_ID), any(CommentRequest.CreateCommentDto.class)))
                .thenReturn(response);

        ResultActions actions = performRequest(patch(BASE_URL_WITH_COMMENT, TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID), request);
        verifySuccessResponse(actions, response);
    }

    @Test
    @DisplayName("댓글 삭제 기능 테스트")
    @WithMockUser(roles = "USER")
    void delete_test() throws Exception {
        doNothing().when(commentService).deleteComment(sessionMember.getId(), TEST_COMMENT_ID);
        ResultActions actions = performRequest(delete(BASE_URL_WITH_COMMENT, TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID), null);
        verifySuccessResponse(actions, null);
    }

    private CommentRequest.CreateCommentDto createCommentDto(String content, Long parentId) {
        return CommentRequest.CreateCommentDto.builder()
                .content(content)
                .parentId(parentId)
                .build();
    }

    private ResultActions performRequest(MockHttpServletRequestBuilder requestBuilder, Object content) throws Exception {
        return mvc.perform(requestBuilder
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(mockHttpSession)
                .with(csrf()));
    }

    private <T> void verifySuccessResponse(ResultActions actions, T expected) throws Exception {
        if (expected instanceof CommentResponse.CommentResponseDTO) {
            actions.andExpect(status().isOk())
                    .andExpect(jsonPath("$.isSuccess").value(true))
                    .andExpect(jsonPath("$.result.content").value(((CommentResponse.CommentResponseDTO) expected).getContent()))
                    .andDo(print());
        }
    }

    private static CommentResponse.CommentResponseDTO createCommentResponseDTO(CommentRequest.CreateCommentDto request) {
        return CommentResponse.CommentResponseDTO.builder()
                .commentId(TEST_COMMENT_ID)
                .content(request.getContent())
                .parentId((request.getParentId() == null) ? null : request.getParentId())
                .build();
    }
}