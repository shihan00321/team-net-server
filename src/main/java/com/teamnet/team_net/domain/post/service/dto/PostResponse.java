package com.teamnet.team_net.domain.post.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.web.PagedModel;

import java.time.LocalDateTime;

public class PostResponse {
    @Getter
    @Builder
    public static class PostResponseDto {
        Long id;
        String title;
        String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt;
        Boolean isMine;
        String createdBy;
    }

    @Getter
    @Builder
    public static class PostListResponseDto {
        PagedModel<PostResponseDto> posts;
    }

}
