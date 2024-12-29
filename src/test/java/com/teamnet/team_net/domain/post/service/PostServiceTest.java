package com.teamnet.team_net.domain.post.service;

import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Test
    @DisplayName("게시글 단건 조회 성공 테스트")
    void 게시글_단건_조회_성공() {
        // given
        Post savedPost = postRepository.save(Post.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build());

        // when
        PostResponse.PostResponseDto foundPost = postService.findOne(savedPost.getId());

        // then
        assertThat(foundPost).isNotNull();
        assertThat(foundPost.getId()).isEqualTo(savedPost.getId());
        assertThat(foundPost.getTitle()).isEqualTo("테스트 제목");
        assertThat(foundPost.getContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회시 예외 발생 테스트")
    void 게시글_단건_조회_예외() {
        // given
        Long nonExistentId = 999L;

        // when & then
        assertThrows(IllegalStateException.class,
                () -> postService.findOne(nonExistentId));
    }

    @Test
    @DisplayName("게시글 모두 조회")
    void 게시글_모두_조회() {
        List<Post> posts = IntStream.range(1, 6)
                .mapToObj(i -> Post.builder()
                        .title("테스트 제목" + i)
                        .content("테스트 내용" + i)
                        .build())
                .toList();
        postRepository.saveAll(posts);

        List<PostResponse.PostResponseDto> findAll = postService.findAll();
        Assertions.assertThat(findAll.size()).isEqualTo(5);
        Assertions.assertThat(findAll.get(2).getTitle()).isEqualTo("테스트 제목" + 3);
        Assertions.assertThat(findAll.get(2).getContent()).isEqualTo("테스트 내용" + 3);
    }

    @Test
    @DisplayName("게시글 저장 테스트")
    public void 게시글_저장_테스트() {
        PostRequest.PostSaveDto postSaveDto = PostRequest.PostSaveDto.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        Long savedPostId = postService.save(postSaveDto);
        assertNotNull(savedPostId);

        Post savedPost = postRepository.findById(savedPostId).orElse(null);
        assertNotNull(savedPost);
        assertEquals("테스트 제목", savedPost.getTitle());
    }

    @Test
    @DisplayName("게시글 수정 테스트")
    void 게시글_수정_테스트() {
        PostRequest.PostSaveDto savedPost = PostRequest.PostSaveDto.builder()
                .title("테스트 제목")
                .title("테스트 내용")
                .build();
        Long savedId = postService.save(savedPost);

        PostRequest.PostUpdateDto updateRequest = PostRequest.PostUpdateDto.builder()
                .title("수정된 제목")
                .content("테스트 내용")
                .build();
        Long updatedId = postService.update(savedId, updateRequest);
        PostResponse.PostResponseDto response = postService.findOne(updatedId);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(updatedId);
        assertThat(response.getTitle()).isEqualTo("수정된 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");
    }

    @Test
    @DisplayName("존재하지 않는 게시글 업데이트 시 예외")
    void 게시글_업데이트_예외() {
        Long nonExistentId = 999L;
        PostRequest.PostUpdateDto updateDto = PostRequest.PostUpdateDto.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> postService.update(nonExistentId, updateDto));
    }
}