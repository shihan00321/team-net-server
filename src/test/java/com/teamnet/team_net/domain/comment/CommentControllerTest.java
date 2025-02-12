package com.teamnet.team_net.domain.comment;

import com.teamnet.team_net.domain.ControllerTestSupport;
import com.teamnet.team_net.domain.comment.controller.CommentRequest;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CommentControllerTest extends ControllerTestSupport {

    private static final Long TEST_TEAM_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    private static final Long TEST_COMMENT_ID = 1L;
    private static final String BASE_URL = "/api/teams/{teamId}/posts/{postId}/comments";
    private static final String BASE_URL_WITH_COMMENT = BASE_URL + "/{commentId}";
    private static final String TEST_CONTENT = "테스트 댓글";

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
    @DisplayName("게시글에 댓글을 등록한다.")
    @WithMockUser(roles = "USER")
    void createCommentTest() throws Exception {
        CommentRequest.CreateCommentDto request = createCommentDto(TEST_CONTENT, null);
        CommentResponse.CommentResponseDTO response = CommentResponse.CommentResponseDTO.builder()
                .commentId(TEST_COMMENT_ID)
                .content(request.getContent())
                .parentId((request.getParentId() == null) ? null : request.getParentId())
                .build();

        when(commentService.createComment(
                eq(sessionMember.getId()), eq(TEST_TEAM_ID), eq(TEST_POST_ID), any(CommentServiceDTO.CreateCommentServiceDto.class)
        )).thenReturn(response);

        mvc.perform(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.content").value(response.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글을 등록할 때 내용은 필수이다.")
    @WithMockUser(roles = "USER")
    void createCommentWithoutContent() throws Exception {
        // given
        CommentRequest.CreateCommentDto request = createCommentDto(null, null);

        // when
        // then
        mvc.perform(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("댓글은 비어있을 수 없습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 하위에 대댓글 등록한다.")
    @WithMockUser(roles = "USER")
    void creatSubComment() throws Exception {
        CommentRequest.CreateCommentDto request = createCommentDto(TEST_CONTENT, 1L);
        CommentResponse.CommentResponseDTO response = CommentResponse.CommentResponseDTO.builder()
                .commentId(TEST_COMMENT_ID)
                .content(request.getContent())
                .parentId((request.getParentId() == null) ? null : request.getParentId())
                .build();

        when(commentService.createComment(eq(sessionMember.getId()), eq(TEST_TEAM_ID), eq(TEST_POST_ID), any(CommentServiceDTO.CreateCommentServiceDto.class)))
                .thenReturn(response);

        mvc.perform(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.content").value(response.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("해당 게시글의 댓글 리스트를 조회한다.")
    @WithMockUser(roles = "USER")
    void findAllComments() throws Exception {
        List<CommentResponse.CommentResponseDTO> posts = IntStream.rangeClosed(1, 2)
                .mapToObj(i -> CommentResponse.CommentResponseDTO.builder()
                        .content(TEST_CONTENT + i)
                        .commentId((long) i)
                        .createdAt(LocalDateTime.now())
                        .parentId(null)
                        .childrenComment(List.of(
                                CommentResponse.CommentResponseDTO.builder()
                                        .commentId((long) i + 5)
                                        .content("대댓글" + i)
                                        .parentId((long) i)
                                        .createdAt(LocalDateTime.now())
                                        .build()
                        ))
                        .build())
                .collect(Collectors.toList());
        PageRequest pageRequest = PageRequest.of(0, 10);
        PageImpl<CommentResponse.CommentResponseDTO> pageResult = new PageImpl<>(
                posts,
                pageRequest,
                10
        );
        PagedModel<CommentResponse.CommentResponseDTO> pagedModel = new PagedModel<>(pageResult);
        CommentResponse.CommentListResponseDTO response = CommentResponse.CommentListResponseDTO.builder().comments(pagedModel).build();

        when(commentService.findComments(anyLong(), anyLong(), any(Pageable.class)))
                .thenReturn(response);

        mvc.perform(get(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.comments.content[1].content").value("테스트 댓글2"));
    }

    @Test
    @DisplayName("게시글을 수정할 때 내용은 필수이다.")
    @WithMockUser(roles = "USER")
    void updateCommentWithoutContent() throws Exception {
        // given
        CommentRequest.UpdateCommentDto request = createUpdateCommentDto(null);

        // when
        // then
        mvc.perform(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.isSuccess").value(false))
                .andExpect(jsonPath("$.message").value("댓글은 비어있을 수 없습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("댓글 작성자가 기존에 작성한 댓글의 내용을 수정한다.")
    @WithMockUser(roles = "USER")
    void update_test() throws Exception {
        CommentRequest.UpdateCommentDto request = createUpdateCommentDto("수정된 댓글");
        CommentResponse.CommentResponseDTO response = CommentResponse.CommentResponseDTO.builder()
                .commentId(TEST_COMMENT_ID)
                .content(request.getContent())
                .build();

        when(commentService.updateComment(eq(sessionMember.getId()), eq(TEST_COMMENT_ID), any(CommentServiceDTO.UpdateCommentServiceDto.class)))
                .thenReturn(response);

        mvc.perform(patch(BASE_URL_WITH_COMMENT, TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.content").value(response.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("댓글 작성자가 기존에 작성한 댓글을 삭제한다.")
    @WithMockUser(roles = "USER")
    void delete_test() throws Exception {
        doNothing().when(commentService).deleteComment(sessionMember.getId(), TEST_COMMENT_ID);
        mvc.perform(delete(BASE_URL_WITH_COMMENT, TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(print());
    }

    private CommentRequest.CreateCommentDto createCommentDto(String content, Long parentId) {
        return CommentRequest.CreateCommentDto.builder()
                .content(content)
                .parentId(parentId)
                .build();
    }

    private CommentRequest.UpdateCommentDto createUpdateCommentDto(String content) {
        return CommentRequest.UpdateCommentDto.builder()
                .content(content)
                .build();
    }
}