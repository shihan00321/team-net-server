package com.teamnet.team_net.domain.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private PostService postService;
    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("게시글 조회 테스트")
    void 게시글_조회_테스트() throws Exception {
        Long postId = 1L;
        PostResponse.PostResponseDto responseDto = PostResponse.PostResponseDto.builder()
                .id(postId)
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        when(postService.findOne(postId)).thenReturn(responseDto);

        mvc.perform(get("/api/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.content").value("테스트 내용"))
                .andDo(print());  // 테스트 결과를 콘솔에 출력
    }

    @Test
    @DisplayName("게시글 모두 조회 테스트")
    void 게시글_모두_조회() throws Exception {
        List<PostResponse.PostResponseDto> posts = new ArrayList<>();
        IntStream.rangeClosed(1, 5).forEach(i -> posts.add(
                PostResponse.PostResponseDto.builder()
                        .id((long) i)
                        .title("테스트 제목" + i)
                        .content("테스트 내용" + i)
                        .build())
        );

        when(postService.findAll()).thenReturn(posts);

        mvc.perform(get("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("테스트 제목2"))
                .andExpect(jsonPath("$[1].content").value("테스트 내용2"))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    void 게시글_저장_성공() throws Exception {
        PostRequest.PostSaveDto request = PostRequest.PostSaveDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        Long requestId = 1L;
        when(postService.save(any(PostRequest.PostSaveDto.class))).thenReturn(requestId);

        mvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());
    }
    @Test
    @DisplayName("제목이 빈 값일 경우 예외")
    void 제목_빈값_예외() throws Exception {
        // given
        PostRequest.PostSaveDto requestDto = PostRequest.PostSaveDto.builder()
                .title("")
                .content("테스트 내용")
                .build();

        // when & then
        mvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("내용이 빈 값일 경우 예외")
    void 내용_빈값_예외() throws Exception {
        PostRequest.PostSaveDto requestDto = PostRequest.PostSaveDto.builder()
                .title("테스트 제목")
                .content("")
                .build();

        mvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    public void 게시글_수정_테스트() throws Exception {
        // Given: 수정할 게시글 DTO
        PostRequest.PostUpdateDto updateDto = PostRequest.PostUpdateDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();
        Long updatedId = 1L;

        when(postService.update(eq(updatedId), any(PostRequest.PostUpdateDto.class))).thenReturn(updatedId);

        mvc.perform(patch("/api/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(updatedId.toString()))
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 삭제 기능 테스트")
    void 게시글_삭제_테스트() throws Exception {
        Long deleteId = 1L;
        when(postService.delete(deleteId)).thenReturn(deleteId);
        mvc.perform(delete("/api/posts/{postId}", deleteId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"))
                .andDo(print());
    }
}
