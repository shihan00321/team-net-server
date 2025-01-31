package com.teamnet.team_net.docs.comment;

import com.teamnet.team_net.docs.RestDocsSupport;
import com.teamnet.team_net.domain.comment.controller.CommentController;
import com.teamnet.team_net.domain.comment.controller.CommentRequest;
import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerDocsTest extends RestDocsSupport {

    private static final Long TEST_TEAM_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    private static final Long TEST_COMMENT_ID = 1L;
    private static final String BASE_URL = "/api/teams/{teamId}/posts/{postId}/comments";
    private static final String TEST_CONTENT = "테스트 댓글";

    private final CommentService commentService = mock(CommentService.class);

    @Override
    protected Object initController() {
        return new CommentController(commentService);
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
                .createdAt(LocalDateTime.now())
                .childrenComment(List.of(CommentResponse.CommentResponseDTO.builder()
                        .commentId(2L)
                        .parentId(TEST_COMMENT_ID)
                        .content("대댓글")
                        .createdAt(LocalDateTime.of(2025, 1, 30, 0, 0, 0))
                        .build()))
                .build();

        given(commentService.createComment(
                anyLong(), anyLong(), anyLong(), any(CommentServiceDTO.CreateCommentServiceDto.class)
        )).willReturn(response);

        mvc.perform(post(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.content").value(response.getContent()))
                .andDo(document("comment-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("parentId").type(JsonFieldType.NUMBER)
                                        .description("부모 댓글 ID")
                                        .optional(),
                                fieldWithPath("content").type(JsonFieldType.STRING)
                                        .description("댓글 내용")
                        ),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부"),
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답에 대한 메세지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터")
                                        .optional(),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("result.parentId").type(JsonFieldType.NUMBER)
                                        .description("부모 댓글 ID (대댓글의 경우").optional(),
                                fieldWithPath("result.content").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                        .description("댓글 ID"),
                                fieldWithPath("result.childrenComment").type(JsonFieldType.ARRAY)
                                        .description("대댓글 목록").optional(),
                                fieldWithPath("result.childrenComment[].commentId").type(JsonFieldType.NUMBER)
                                        .description("대댓글 ID"),
                                fieldWithPath("result.childrenComment[].parentId").type(JsonFieldType.NUMBER)
                                        .description("부모 댓글 ID"),
                                fieldWithPath("result.childrenComment[].content").type(JsonFieldType.STRING)
                                        .description("대댓글 내용"),
                                fieldWithPath("result.childrenComment[].createdAt").type(JsonFieldType.STRING)
                                        .description("대댓글 생성 시간")
                        )
                ));
    }

    private CommentRequest.CreateCommentDto createCommentDto(String content, Long parentId) {
        return CommentRequest.CreateCommentDto.builder()
                .content(content)
                .parentId(parentId)
                .build();
    }
}
