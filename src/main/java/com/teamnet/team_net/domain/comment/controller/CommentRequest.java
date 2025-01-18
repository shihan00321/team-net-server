package com.teamnet.team_net.domain.comment.controller;

import lombok.*;

public class CommentRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CreateCommentDto {
        Long parentId;
        String content;
    }
}
