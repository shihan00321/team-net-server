package com.teamnet.team_net.domain.comment.controller;

import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class CommentRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateCommentDto {
        Long parentId;

        @NotBlank(message = "댓글은 비어있을 수 없습니다.")
        String content;

        protected CommentServiceDTO.CreateCommentServiceDto toCommentServiceDTO() {
            return CommentServiceDTO.CreateCommentServiceDto
                    .builder()
                    .parentId(parentId)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateCommentDto {
        Long parentId;

        @NotBlank(message = "댓글은 비어있을 수 없습니다.")
        String content;

        protected CommentServiceDTO.UpdateCommentServiceDto toCommentServiceDTO() {
            return CommentServiceDTO.UpdateCommentServiceDto
                    .builder()
                    .parentId(parentId)
                    .content(content)
                    .build();
        }
    }
}
