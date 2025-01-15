package com.teamnet.team_net.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.PostService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PostController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
})
class PostControllerTest {

    @Autowired
    private MockMvc mvc;
    @MockBean
    private PostService postService;
    @Autowired
    private ObjectMapper objectMapper;
    @Mock
    private HttpSession httpSession;
    private SessionMember sessionMember;

    @BeforeEach
    void setUp() {
        sessionMember = new SessionMember(Member.builder()
                .id(1L)
                .nickname("hbb")
                .build());
        when(httpSession.getAttribute("member")).thenReturn(sessionMember);
    }


    @Test
    @DisplayName("게시글 조회 테스트")
    @WithMockUser(roles = "USER")
    void 게시글_조회_테스트() throws Exception {
        Long postId = 1L;
        Long teamId = 1L;
        PostResponse.PostResponseDto responseDto = PostResponse.PostResponseDto.builder()
                .id(postId)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        when(postService.findOne(postId)).thenReturn(responseDto);

        mvc.perform(get("/api/teams/{teamId}/posts/{postId}", teamId, postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.id").value(1L))
                .andExpect(jsonPath("$.result.title").value("테스트 제목"))
                .andExpect(jsonPath("$.result.content").value("테스트 내용"))
                .andDo(print());  // 테스트 결과를 콘솔에 출력
    }

    @Test
    @DisplayName("게시글 모두 조회 테스트")
    @WithMockUser(roles = "USER")
    void 게시글_모두_조회() throws Exception {
        List<PostResponse.PostResponseDto> posts = new ArrayList<>();
        IntStream.rangeClosed(1, 5).forEach(i -> posts.add(
                PostResponse.PostResponseDto.builder()
                        .id((long) i)
                        .title("테스트 제목" + i)
                        .content("테스트 내용" + i)
                        .build())
        );
        Long teamId = 1L;

        when(postService.findAllByTeamId(teamId)).thenReturn(posts);

        mvc.perform(get("/api/teams/{teamId}/posts", teamId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result", hasSize(5)))
                .andExpect(jsonPath("$.result[1].id").value(2L))
                .andExpect(jsonPath("$.result[1].title").value("테스트 제목2"))
                .andExpect(jsonPath("$.result[1].content").value("테스트 내용2"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    @WithMockUser(roles = "USER")
    void 게시글_저장_성공() throws Exception {
        PostRequest.PostSaveDto request = PostRequest.PostSaveDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        Long teamId = 1L;
        Long postId = 1L;

        given(postService.save(eq(sessionMember.getId()), eq(teamId), any(PostRequest.PostSaveDto.class))).willReturn(sessionMember.getId());

        mvc.perform(post("/api/teams/{teamId}/posts", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result").value(postId))
                .andDo(print());
    }

    @Test
    @WithMockUser
    void save_validation_fail() throws Exception {
        PostRequest.PostSaveDto requestDto = new PostRequest.PostSaveDto("", "");  // 유효성 검사 실패용 데이터
        Long teamId = 1L;

        mvc.perform(post("/api/teams/{teamId}/posts", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .sessionAttr("member", sessionMember)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("제목이 빈 값일 경우 예외")
    @WithMockUser(roles = "USER")
    void 제목_빈값_예외() throws Exception {
        Map<String, Object> requestDto = new HashMap<>();
        requestDto.put("title", "");
        requestDto.put("content", "테스트 내용");
        Long teamId = 1L;

        // when & then
        mvc.perform(post("/api/teams/{teamId}/posts", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("내용이 빈 값일 경우 예외")
    @WithMockUser(roles = "USER")
    void 내용_빈값_예외() throws Exception {
        PostRequest.PostSaveDto requestDto = PostRequest.PostSaveDto.builder()
                .title("테스트 제목")
                .content("")
                .build();
        Long teamId = 1L;

        mvc.perform(post("/api/teams/{teamId}/posts", teamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    @WithMockUser(roles = "USER")
    public void 게시글_수정_테스트() throws Exception {
        // Given: 수정할 게시글 DTO
        PostRequest.PostUpdateDto updateDto = PostRequest.PostUpdateDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();
        Long updatedId = 1L;
        Long memberId = 1L;
        Long postId = 1L;

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        when(postService.update(eq(memberId), eq(updatedId), any(PostRequest.PostUpdateDto.class))).thenReturn(updatedId);

        mvc.perform(patch("/api/teams/{teamId}/posts/{postId}", 1L, postId)
                        .session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(postId))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제 기능 테스트")
    @WithMockUser(roles = "USER")
    void 게시글_삭제_테스트() throws Exception {
        Long deleteId = 1L;
        Long memberId = 1L;
        // MockHttpSession 생성 및 사용자 정보 설정
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("member", sessionMember);

        when(postService.delete(memberId, deleteId)).thenReturn(deleteId);

        mvc.perform(delete("/api/teams/{teamId}/posts/{postId}", 1L, deleteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(deleteId))
                .andDo(print());
    }
}
