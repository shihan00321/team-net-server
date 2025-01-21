package com.teamnet.team_net.domain.comment.service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponse {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class CommentResponseDTO {
        Long commentId;
        Long parentId;
        String content;
        LocalDateTime createdAt;
        List<CommentResponseDTO> childrenComment;
    }
}
