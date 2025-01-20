package com.teamnet.team_net.domain.post.dto;

import com.teamnet.team_net.domain.comment.dto.CommentResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class PostResponse {
    @Getter
    @Builder
    public static class PostResponseDto {
        Long id;
        String title;
        String content;
        List<CommentResponse.CommentResponseDTO> comments;
    }

    @Getter
    @Builder
    public static class PostListResponseDto {
        List<PostResponseDto> posts;
    }

}
