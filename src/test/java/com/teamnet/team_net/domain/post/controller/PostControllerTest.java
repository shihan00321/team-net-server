package com.teamnet.team_net.domain.post.controller;

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
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private PostService postService;

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
}