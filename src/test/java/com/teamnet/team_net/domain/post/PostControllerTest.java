package com.teamnet.team_net.domain.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.post.controller.PostController;
import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class PostControllerTest {
    private static final Long TEST_TEAM_ID = 1L;
    private static final Long TEST_POST_ID = 1L;
    private static final String BASE_URL = "/api/teams/{teamId}/posts";
    private static final String TEST_TITLE = "테스트 제목";
    private static final String TEST_CONTENT = "테스트 내용";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PostService postService;
    @Autowired
    private ObjectMapper objectMapper;
    private SessionMember sessionMember;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        sessionMember = new SessionMember(Member.builder()
                .id(1L)
                .nickname("hbb")
                .name("hbb")
                .email("xxx.xxx.com")
                .role(Role.USER)
                .status(DeletionStatus.NOT_DELETE)
                .build());
        mockSession = new MockHttpSession();
        mockSession.setAttribute("member", sessionMember);
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
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.title").value(TEST_TITLE),
                            jsonPath("$.result.content").value(TEST_CONTENT)
                    ).andDo(print());
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
            PostResponse.PostListResponseDto response = PostResponse.PostListResponseDto.builder().posts(pageResult).build();

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
                            .session(mockSession)
                            .with(csrf()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.posts.content", hasSize(5)),
                            jsonPath("$.result.posts.content[1].id").value(2L),
                            jsonPath("$.result.posts.content[1].title").value(TEST_TITLE + "2"),
                            jsonPath("$.result.posts.content[1].content").value(TEST_CONTENT + "2"),
                            jsonPath("$.result.posts.page.size").value(10),
                            jsonPath("$.result.posts.page.number").value(0),
                            jsonPath("$.result.posts.page.totalPages").value(1)
                    ).andDo(print());
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
                            .session(mockSession)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))

                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.isSuccess").value(true),
                            jsonPath("$.result.title").value(responseDto.getTitle()),
                            jsonPath("$.result.content").value(responseDto.getContent())
                    ).andDo(print());
        }

        @Test
        @DisplayName("신규 게시글 등록 시 제목은 필수이다.")
        @WithMockUser(roles = "USER")
        void postSaveWithoutPostTitle() throws Exception {
            // given
            PostRequest.PostSaveDTO request = createPostSaveDto("", TEST_CONTENT);

            // when & then
            mvc.perform(post(BASE_URL, TEST_TEAM_ID)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.isSuccess").value(false),
                            jsonPath("$.message").value("제목은 비어있을 수 없습니다.")
                    )
                    .andDo(print());
        }

        @Test
        @DisplayName("신규 게시글 등록 시 내용은 필수이다.")
        @WithMockUser(roles = "USER")
        void postSaveWithoutPostContent() throws Exception {
            // given
            PostRequest.PostSaveDTO request = createPostSaveDto(TEST_TITLE, "");

            // when & then
            mvc.perform(post(BASE_URL, TEST_TEAM_ID)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .with(csrf()))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.isSuccess").value(false),
                            jsonPath("$.message").value("내용은 비어있을 수 없습니다.")
                    ).andDo(print());
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
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                            .with(csrf()))
                    .andExpectAll(
                            status().isOk(),
                            jsonPath("$.result.title").value(responseDto.getTitle()),
                            jsonPath("$.result.content").value(responseDto.getContent())
                    ).andDo(print());
        }

        @Test
        @DisplayName("게시글 수정 시 제목은 필수이다.")
        @WithMockUser(roles = "USER")
        void updatePostWithoutTitle() throws Exception {
            // given
            PostRequest.PostUpdateDTO updateDto = PostRequest.PostUpdateDTO.builder()
                    .title("")
                    .content("수정된 내용")
                    .build();

            //when & then
            mvc.perform(patch(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                            .with(csrf()))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.isSuccess").value(false),
                            jsonPath("$.message").value("제목은 비어있을 수 없습니다.")
                    ).andDo(print());
        }

        @Test
        @DisplayName("게시글을 수정 시 내용은 필수이다.")
        @WithMockUser(roles = "USER")
        void update() throws Exception {
            // given
            PostRequest.PostUpdateDTO updateDto = PostRequest.PostUpdateDTO.builder()
                    .title("수정된 제목")
                    .content("")
                    .build();

            //when & then
            mvc.perform(patch(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID)
                            .session(mockSession)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto))
                            .with(csrf()))
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.isSuccess").value(false),
                            jsonPath("$.message").value("내용은 비어있을 수 없습니다.")
                    ).andDo(print());
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
                        .session(mockSession)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private PostResponse.PostResponseDto createPostResponseDto(Long id, String title, String content) {
        return PostResponse.PostResponseDto.builder()
                .id(id)
                .title(title)
                .content(content)
                .build();
    }

    private PostRequest.PostSaveDTO createPostSaveDto(String title, String content) {
        return PostRequest.PostSaveDTO.builder()
                .title(title)
                .content(content)
                .build();
    }
}
