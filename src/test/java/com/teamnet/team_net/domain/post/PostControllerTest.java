package com.teamnet.team_net.domain.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.controller.PostController;
import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
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
import org.springframework.test.web.servlet.ResultActions;

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
    @Mock
    private HttpSession httpSession;
    private SessionMember sessionMember;
    private MockHttpSession mockSession;

    @BeforeEach
    void setUp() {
        sessionMember = new SessionMember(Member.builder()
                .id(1L)
                .nickname("hbb")
                .build());
        mockSession = new MockHttpSession();
        mockSession.setAttribute("member", sessionMember);
        when(httpSession.getAttribute("member")).thenReturn(sessionMember);
    }

    @Test
    @DisplayName("게시글 조회 테스트")
    @WithMockUser(roles = "USER")
    void findOne() throws Exception {
        PostResponse.PostResponseDto responseDto = createPostResponseDto(TEST_POST_ID, TEST_TITLE, TEST_CONTENT);
        when(postService.findOne(TEST_POST_ID)).thenReturn(responseDto);

        performGetRequest(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.id").value(TEST_POST_ID))
                .andExpect(jsonPath("$.result.title").value(TEST_TITLE))
                .andExpect(jsonPath("$.result.content").value(TEST_CONTENT))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 모두 조회 테스트")
    @WithMockUser(roles = "USER")
    void findAll() throws Exception {
        List<PostResponse.PostResponseDto> posts = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> createPostResponseDto((long) i, TEST_TITLE + i, TEST_CONTENT + i))
                .collect(Collectors.toList());
        PostResponse.PostListResponseDto response = PostResponse.PostListResponseDto.builder().posts(posts).build();

        when(postService.findAllByTeamId(TEST_TEAM_ID)).thenReturn(response);

        performGetRequest(BASE_URL, TEST_TEAM_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.posts", hasSize(5)))
                .andExpect(jsonPath("$.result.posts[1].id").value(2L))
                .andExpect(jsonPath("$.result.posts[1].title").value(TEST_TITLE + "2"))
                .andExpect(jsonPath("$.result.posts[1].content").value(TEST_CONTENT + "2"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    @WithMockUser(roles = "USER")
    void save() throws Exception {
        PostRequest.PostSaveDTO request = createPostSaveDto(TEST_TITLE, TEST_CONTENT);
        PostResponse.PostResponseDto responseDto = createPostResponseDto(TEST_POST_ID, TEST_TITLE, TEST_CONTENT);
        given(postService.save(eq(sessionMember.getId()), eq(TEST_TEAM_ID), any(PostServiceDTO.PostSaveServiceDTO.class)))
                .willReturn(responseDto);

        performPostRequest(request, TEST_TEAM_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.title").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.result.content").value(responseDto.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("유효성 검증 실패 테스트")
    @WithMockUser(roles = "USER")
    void validation_test() throws Exception {
        // 제목 빈 값 테스트
        performPostRequest(createPostSaveDto("", TEST_CONTENT), TEST_TEAM_ID)
                .andExpect(status().isBadRequest())
                .andDo(print());

        // 내용 빈 값 테스트
        performPostRequest(createPostSaveDto(TEST_TITLE, ""), TEST_TEAM_ID)
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @WithMockUser(roles = "USER")
    void update() throws Exception {
        PostResponse.PostResponseDto responseDto = createPostResponseDto(TEST_POST_ID, TEST_TITLE, TEST_CONTENT);
        PostRequest.PostUpdateDTO updateDto = PostRequest.PostUpdateDTO.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        when(postService.update(eq(sessionMember.getId()), eq(TEST_POST_ID), any(PostServiceDTO.PostUpdateServiceDTO.class)))
                .thenReturn(responseDto);

        mvc.perform(patch(BASE_URL + "/{postId}", TEST_TEAM_ID, TEST_POST_ID)
                        .session(mockSession)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.title").value(responseDto.getTitle()))
                .andExpect(jsonPath("$.result.content").value(responseDto.getContent()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제 테스트")
    @WithMockUser(roles = "USER")
    void delete_test() throws Exception {
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

    private ResultActions performGetRequest(String url, Object... urlVariables) throws Exception {
        return mvc.perform(get(url, urlVariables)
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions performPostRequest(Object content, Object... urlVariables) throws Exception {
        return mvc.perform(post(PostControllerTest.BASE_URL, urlVariables)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
                .session(mockSession)
                .with(csrf()));
    }
}
