package com.teamnet.team_net.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private static final String BASE_URL = "/api/teams/{teamId}/posts";
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
        CommentRequest.CreateCommentDto request = CommentRequest.CreateCommentDto.builder()
                .content(TEST_CONTENT)
                .build();

        when(commentService.createComment(eq(sessionMember.getId()), eq(TEST_TEAM_ID), eq(TEST_POST_ID), any(CommentRequest.CreateCommentDto.class))).thenReturn(TEST_COMMENT_ID);

        mvc.perform(post("/api/teams/{teamId}/posts/{postId}/comments", TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(TEST_COMMENT_ID))
                .andDo(print());
    }



    @Test
    @DisplayName("대댓글 생성 기능 테스트")
    @WithMockUser(roles = "USER")
    void create_in_comment_test() throws Exception {
        CommentRequest.CreateCommentDto request = CommentRequest.CreateCommentDto.builder()
                .parentId(1L)
                .content(TEST_CONTENT)
                .build();

        when(commentService.createComment(eq(sessionMember.getId()), eq(TEST_TEAM_ID), eq(TEST_POST_ID), any(CommentRequest.CreateCommentDto.class))).thenReturn(TEST_COMMENT_ID);

        mvc.perform(post("/api/teams/{teamId}/posts/{postId}/comments", TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(TEST_COMMENT_ID))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 수정 기능 테스트")
    @WithMockUser(roles = "USER")
    void update_test() throws Exception {
        CommentRequest.CreateCommentDto request = CommentRequest.CreateCommentDto.builder()
                .content("수정된 댓글")
                .build();

        when(commentService.updateComment(eq(sessionMember.getId()), eq(TEST_COMMENT_ID), any(CommentRequest.CreateCommentDto.class)))
                .thenReturn(TEST_COMMENT_ID);

        mvc.perform(patch("/api/teams/{teamId}/posts/{postId}/comments/{commentId}",
                        TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(TEST_COMMENT_ID))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 삭제 기능 테스트")
    @WithMockUser(roles = "USER")
    void delete_test() throws Exception {
        when(commentService.deleteComment(sessionMember.getId(), TEST_COMMENT_ID))
                .thenReturn(TEST_COMMENT_ID);

        // when & then
        mvc.perform(delete("/api/teams/{teamId}/posts/{postId}/comments/{commentId}",
                        TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(TEST_COMMENT_ID))
                .andDo(print());
    }
}