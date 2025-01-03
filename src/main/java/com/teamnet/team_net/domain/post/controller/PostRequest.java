package com.teamnet.team_net.domain.post.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

public class PostRequest {

    @Getter
    public static class PostSaveDto {
        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title;
        @NotBlank(message = "내용은 비어있을 수 없습니다.")
        String content;
    }

    @Getter
    public static class PostUpdateDto {
        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title;
        @NotBlank(message = "내용은 비어있을 수 없습니다.")
        String content;
    }
}
