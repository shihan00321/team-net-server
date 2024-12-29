package com.teamnet.team_net.domain.post.service;

import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.mapper.PostMapper;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponse.PostResponseDto findOne(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(IllegalStateException::new);

        return PostMapper.toPostResponseDto(post);
    }

    public List<PostResponse.PostResponseDto> findAll() {
        return postRepository.findAll().stream()
                .map(post -> PostResponse.PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    public Long save(PostRequest.PostSaveDto postSaveDto) {
        Post savedPost = postRepository.save(Post.builder()
                .title(postSaveDto.getTitle())
                .content(postSaveDto.getContent())
                .build());
        return savedPost.getId();
    }
}
