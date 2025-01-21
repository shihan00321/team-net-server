package com.teamnet.team_net.domain.comment.service.dto;

import lombok.*;

public class CommentServiceDTO {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateCommentServiceDto {
        Long parentId;
        String content;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateCommentServiceDto {
        Long parentId;
        String content;
    }
}
