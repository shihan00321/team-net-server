package com.teamnet.team_net.domain.post.service;

import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.mapper.PostMapper;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;

    public PostResponse.PostResponseDto findOne(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(IllegalStateException::new);

        return PostMapper.toPostResponseDto(post);
    }
}
