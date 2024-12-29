package com.teamnet.team_net.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

public class PostResponse {
    @Getter
    @Builder
    public static class PostResponseDto {
        Long id;
        String title;
        String content;
    }
}
