package com.teamnet.team_net.domain.post.service.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

public class PostResponse {
    @Getter
    @Builder
    public static class PostResponseDto {
        Long id;
        String title;
        String content;
    }

    @Getter
    @Builder
    public static class PostListResponseDto {
        Page<PostResponseDto> posts;
    }

}
