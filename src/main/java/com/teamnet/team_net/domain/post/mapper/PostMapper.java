package com.teamnet.team_net.domain.post.mapper;

import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.entity.Post;

public class PostMapper {
    public static PostResponse.PostResponseDto toPostResponseDto(Post post) {
        return PostResponse.PostResponseDto
                .builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}
