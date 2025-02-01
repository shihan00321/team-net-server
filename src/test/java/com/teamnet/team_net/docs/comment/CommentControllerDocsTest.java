package com.teamnet.team_net.docs.comment;

import com.teamnet.team_net.docs.RestDocsSupport;
import com.teamnet.team_net.domain.comment.controller.CommentController;
import com.teamnet.team_net.domain.comment.controller.CommentRequest;
import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import org.junit.jupiter.api.DisplayName;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CommentControllerDocsTest extends RestDocsSupport {

    private static final Long TEST_TEAM_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    private static final Long TEST_COMMENT_ID = 1L;
    private static final String BASE_URL = "/api/teams/{teamId}/posts/{postId}/comments";
    private static final String BASE_URL_WITH_COMMENT = BASE_URL + "/{commentId}";
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
                                        .description("응답 데이터"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("result.parentId").type(JsonFieldType.NUMBER)
                                        .description("부모 댓글 ID (대댓글의 경우)").optional(),
                                fieldWithPath("result.content").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                        .description("댓글 ID")
                        )
                ));
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

        when(commentService.findComments(eq(TEST_POST_ID), any(Pageable.class)))
                .thenReturn(response);

        mvc.perform(get(BASE_URL, TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession)
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.comments.content[1].content").value("테스트 댓글2"))
                .andDo(document("comment-find-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부"),
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답에 대한 메세지"),
                                fieldWithPath("result").type(JsonFieldType.OBJECT)
                                        .description("응답 데이터"),
                                fieldWithPath("result.comments.content").type(JsonFieldType.ARRAY)
                                        .description("댓글 목록").optional(),
                                fieldWithPath("result.comments.content[].commentId").type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("result.comments.content[].parentId").type(JsonFieldType.NUMBER)
                                        .description("부모 댓글 ID (대댓글의 경우)").optional(),
                                fieldWithPath("result.comments.content[].content").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("result.comments.content[].createdAt").type(JsonFieldType.STRING)
                                        .description("댓글 ID"),
                                fieldWithPath("result.comments.content[].childrenComment").type(JsonFieldType.ARRAY)
                                        .description("대댓글 목록").optional(),
                                fieldWithPath("result.comments.content[].childrenComment[].commentId").type(JsonFieldType.NUMBER)
                                        .description("대댓글 ID"),
                                fieldWithPath("result.comments.content[].childrenComment[].parentId").type(JsonFieldType.NUMBER)
                                        .description("해당 대댓글의 부모 댓글 ID "),
                                fieldWithPath("result.comments.content[].childrenComment[].content").type(JsonFieldType.STRING)
                                        .description("대댓글 내용"),
                                fieldWithPath("result.comments.content[].childrenComment[].createdAt").type(JsonFieldType.STRING)
                                        .description("대댓글 생성 시간"),
                                fieldWithPath("result.comments.page.size").type(JsonFieldType.NUMBER)
                                        .description("페이지 크기"),
                                fieldWithPath("result.comments.page.number").type(JsonFieldType.NUMBER)
                                        .description("현재 페이지 번호"),
                                fieldWithPath("result.comments.page.totalPages").type(JsonFieldType.NUMBER)
                                        .description("총 페이지 수"),
                                fieldWithPath("result.comments.page.totalElements").type(JsonFieldType.NUMBER)
                                        .description("총 게시글 수")

                        )
                ));
    }

    @Test
    @DisplayName("댓글 작성자가 기존에 작성한 댓글의 내용을 수정한다.")
    @WithMockUser(roles = "USER")
    void update_test() throws Exception {
        CommentRequest.UpdateCommentDto request = createUpdateCommentDto("수정된 댓글");
        CommentResponse.CommentResponseDTO response = CommentResponse.CommentResponseDTO.builder()
                .commentId(TEST_COMMENT_ID)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        when(commentService.updateComment(eq(sessionMember.getId()), eq(TEST_COMMENT_ID), any(CommentServiceDTO.UpdateCommentServiceDto.class)))
                .thenReturn(response);

        mvc.perform(patch(BASE_URL_WITH_COMMENT, TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.content").value(response.getContent()))
                .andDo(document("comment-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
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
                                        .description("응답 데이터"),
                                fieldWithPath("result.commentId").type(JsonFieldType.NUMBER)
                                        .description("댓글 ID"),
                                fieldWithPath("result.parentId").type(JsonFieldType.NUMBER)
                                        .description("부모 댓글 ID (대댓글의 경우)").optional(),
                                fieldWithPath("result.content").type(JsonFieldType.STRING)
                                        .description("댓글 내용"),
                                fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                        .description("댓글 ID")
                        )
                ));
        ;
    }

    @Test
    @DisplayName("댓글 작성자가 기존에 작성한 댓글을 삭제한다.")
    @WithMockUser(roles = "USER")
    void delete_test() throws Exception {
        doNothing().when(commentService).deleteComment(sessionMember.getId(), TEST_COMMENT_ID);
        mvc.perform(delete(BASE_URL_WITH_COMMENT, TEST_TEAM_ID, TEST_POST_ID, TEST_COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andDo(document("comment-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                        .description("성공 여부"),
                                fieldWithPath("code").type(JsonFieldType.STRING)
                                        .description("코드"),
                                fieldWithPath("message").type(JsonFieldType.STRING)
                                        .description("응답에 대한 메세지")
                        )));
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
