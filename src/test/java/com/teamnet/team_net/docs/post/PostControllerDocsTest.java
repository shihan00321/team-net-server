package com.teamnet.team_net.docs.post;


import com.teamnet.team_net.docs.RestDocsSupport;
import com.teamnet.team_net.domain.post.controller.PostController;
import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostControllerDocsTest extends RestDocsSupport {

    private static final Long TEST_TEAM_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    private static final String BASE_URL = "/api/teams/{teamId}/posts";
    private static final String TEST_TITLE = "테스트 제목";
    private static final String TEST_CONTENT = "테스트 내용";

    PostService postService = mock(PostService.class);

    @Override
    protected Object initController() {
        return new PostController(postService);
    }

    @Nested
    @DisplayName("해당 팀의 게시글을 조회한다.")
    class findPostTest {
        @Test
        @DisplayName("해당 팀의 게시글 단건을 조회한다.")
        @WithMockUser(roles = "USER")
        void findPost() throws Exception {
            //given
            PostResponse.PostResponseDto responseDto = createPostResponseDto(TEST_POST_ID, TEST_TITLE, TEST_CONTENT);

            //when & then
            when(postService.findOne(TEST_POST_ID)).thenReturn(responseDto);

            mvc.perform(get(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.title").value(TEST_TITLE),
                            jsonPath("$.result.content").value(TEST_CONTENT)
                    )
                    .andDo(document("post-find",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result.id").type(JsonFieldType.NUMBER)
                                            .description("게시글 ID"),
                                    fieldWithPath("result.title").type(JsonFieldType.STRING)
                                            .description("게시글 제목"),
                                    fieldWithPath("result.content").type(JsonFieldType.STRING)
                                            .description("게시글 내용"),
                                    fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                            .description("게시글 작성 시간")
                            )));
        }

        @Test
        @DisplayName("해당 팀의 게시글을 페이징 처리하여 10개씩 조회한다.")
        @WithMockUser(roles = "USER")
        void findAllPost() throws Exception {
            //given
            List<PostResponse.PostResponseDto> posts = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> createPostResponseDto((long) i, TEST_TITLE + i, TEST_CONTENT + i))
                    .collect(Collectors.toList());
            PageRequest pageRequest = PageRequest.of(0, 10);
            PageImpl<PostResponse.PostResponseDto> pageResult = new PageImpl<>(
                    posts,
                    pageRequest,
                    10
            );
            PagedModel<PostResponse.PostResponseDto> pagedModel = new PagedModel<>(pageResult);
            PostResponse.PostListResponseDto response = PostResponse.PostListResponseDto.builder().posts(pagedModel).build();

            //when & then
            when(postService.findAll(
                    eq(sessionMember.getId()),
                    eq(TEST_TEAM_ID),
                    any(PostServiceDTO.PostSearchKeywordServiceDTO.class),
                    any(Pageable.class)
            )).thenReturn(response);

            mvc.perform(get(BASE_URL, TEST_TEAM_ID)
                            .param("keyword", "테스트")
                            .param("type", "TITLE")
                            .accept(MediaType.APPLICATION_JSON)
                            .session(mockHttpSession))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.posts.content", hasSize(5)),
                            jsonPath("$.result.posts.content[1].id").value(2L),
                            jsonPath("$.result.posts.content[1].title").value(TEST_TITLE + "2"),
                            jsonPath("$.result.posts.content[1].content").value(TEST_CONTENT + "2"),
                            jsonPath("$.result.posts.page.size").value(10),
                            jsonPath("$.result.posts.page.number").value(0),
                            jsonPath("$.result.posts.page.totalPages").value(1))
                    .andDo(document("posts-find-all",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            queryParameters(
                                    parameterWithName("keyword").description("검색 키워드"),
                                    parameterWithName("type").description("검색 타입 (TITLE, CONTENT, TITLE_CONTENT, AUTHOR)")
                            ),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result.posts.content").type(JsonFieldType.ARRAY)
                                            .description("게시글 목록").optional(),
                                    fieldWithPath("result.posts.content[].id").type(JsonFieldType.NUMBER)
                                            .description("게시글 ID"),
                                    fieldWithPath("result.posts.content[].title").type(JsonFieldType.STRING)
                                            .description("게시글 제목"),
                                    fieldWithPath("result.posts.content[].content").type(JsonFieldType.STRING)
                                            .description("게시글 내용"),
                                    fieldWithPath("result.posts.content[].createdAt").type(JsonFieldType.STRING)
                                            .description("게시글 작성 시간"),
                                    fieldWithPath("result.posts.page.size").type(JsonFieldType.NUMBER)
                                            .description("페이지 크기"),
                                    fieldWithPath("result.posts.page.number").type(JsonFieldType.NUMBER)
                                            .description("현재 페이지 번호"),
                                    fieldWithPath("result.posts.page.totalPages").type(JsonFieldType.NUMBER)
                                            .description("총 페이지 수"),
                                    fieldWithPath("result.posts.page.totalElements").type(JsonFieldType.NUMBER)
                                            .description("총 게시글 수")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("새로운 게시글을 등록한다.")
    class savePostTest {
        @Test
        @DisplayName("신규 게시글을 성공적으로 등록한다.")
        @WithMockUser(roles = "USER")
        void savePost() throws Exception {
            //given
            PostRequest.PostSaveDTO request = createPostSaveDto(TEST_TITLE, TEST_CONTENT);
            PostResponse.PostResponseDto responseDto = createPostResponseDto(TEST_POST_ID, TEST_TITLE, TEST_CONTENT);

            //when & then
            given(postService.save(eq(sessionMember.getId()), eq(TEST_TEAM_ID), any(PostServiceDTO.PostSaveServiceDTO.class)))
                    .willReturn(responseDto);

            mvc.perform(post(BASE_URL, TEST_TEAM_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .session(mockHttpSession)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.title").value(responseDto.getTitle()),
                            jsonPath("$.result.content").value(responseDto.getContent())
                    )
                    .andDo(document("post-create",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("게시글 제목"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("게시글 내용")
                            ),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result.id").type(JsonFieldType.NUMBER)
                                            .description("게시글 ID"),
                                    fieldWithPath("result.title").type(JsonFieldType.STRING)
                                            .description("게시글 제목"),
                                    fieldWithPath("result.content").type(JsonFieldType.STRING)
                                            .description("게시글 내용"),
                                    fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                            .description("게시글 작성 시간")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("게시글을 수정한다.")
    class UpdatePostTest {
        @Test
        @DisplayName("게시글을 성공적으로 수정한다.")
        @WithMockUser(roles = "USER")
        void updatePost() throws Exception {
            // given
            PostResponse.PostResponseDto responseDto = createPostResponseDto(TEST_POST_ID, TEST_TITLE, TEST_CONTENT);
            PostRequest.PostUpdateDTO updateDto = PostRequest.PostUpdateDTO.builder()
                    .title("수정된 제목")
                    .content("수정된 내용")
                    .build();

            //when & then
            when(postService.update(
                    eq(sessionMember.getId()),
                    eq(TEST_POST_ID),
                    any(PostServiceDTO.PostUpdateServiceDTO.class))
            ).thenReturn(responseDto);

            mvc.perform(patch(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID)
                            .session(mockHttpSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andDo(print())
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result.title").value(responseDto.getTitle()),
                            jsonPath("$.result.content").value(responseDto.getContent())
                    ).andDo(document("post-update",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestFields(
                                    fieldWithPath("title").type(JsonFieldType.STRING).description("수정된 게시글 제목"),
                                    fieldWithPath("content").type(JsonFieldType.STRING).description("수정된 게시글 내용")
                            ),
                            responseFields(
                                    fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                    fieldWithPath("code").type(JsonFieldType.STRING).description("응답 코드"),
                                    fieldWithPath("message").type(JsonFieldType.STRING).description("응답 메세지"),
                                    fieldWithPath("result.id").type(JsonFieldType.NUMBER)
                                            .description("게시글 ID"),
                                    fieldWithPath("result.title").type(JsonFieldType.STRING)
                                            .description("게시글 제목"),
                                    fieldWithPath("result.content").type(JsonFieldType.STRING)
                                            .description("게시글 내용"),
                                    fieldWithPath("result.createdAt").type(JsonFieldType.STRING)
                                            .description("게시글 작성 시간")
                            )
                    ));
        }
    }

    @Test
    @DisplayName("게시글을 삭제한다.")
    @WithMockUser(roles = "USER")
    void delete_test() throws Exception {
        //when & then
        doNothing().when(postService).delete(sessionMember.getId(), TEST_POST_ID);
        mvc.perform(delete(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(mockHttpSession))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("post-delete",
                        responseFields(
                                fieldWithPath("isSuccess").description("성공 여부"),
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지")
                        )
                ));
    }

    private PostResponse.PostResponseDto createPostResponseDto(Long id, String title, String content) {
        return PostResponse.PostResponseDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private PostRequest.PostSaveDTO createPostSaveDto(String title, String content) {
        return PostRequest.PostSaveDTO.builder()
                .title(title)
                .content(content)
                .build();
    }
}
