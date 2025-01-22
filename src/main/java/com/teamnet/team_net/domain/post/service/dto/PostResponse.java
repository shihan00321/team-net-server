package com.teamnet.team_net.domain.post.service.dto;

import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

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
        Page<PostResponseDto> posts;
    }

}
