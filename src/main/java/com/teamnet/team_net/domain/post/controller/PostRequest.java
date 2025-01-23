package com.teamnet.team_net.domain.post.controller;

import com.teamnet.team_net.domain.post.enums.SearchType;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class PostRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PostSaveDTO {
        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title;
        @NotBlank(message = "내용은 비어있을 수 없습니다.")
        String content;

        protected PostServiceDTO.PostSaveServiceDTO toPostSaveServiceDTO() {
            return PostServiceDTO.PostSaveServiceDTO.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PostUpdateDTO {
        @NotBlank(message = "제목은 비어있을 수 없습니다.")
        String title;
        @NotBlank(message = "내용은 비어있을 수 없습니다.")
        String content;

        protected PostServiceDTO.PostUpdateServiceDTO toPostUpdateServiceDTO() {
            return PostServiceDTO.PostUpdateServiceDTO.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PostSearchKeywordDTO {
        String keyword;
        SearchType type;

        protected PostServiceDTO.PostSearchKeywordServiceDTO toPostSearchKeywordServiceDTO() {
            return PostServiceDTO.PostSearchKeywordServiceDTO.builder()
                    .keyword(keyword)
                    .type(type)
                    .build();
        }
    }
}
