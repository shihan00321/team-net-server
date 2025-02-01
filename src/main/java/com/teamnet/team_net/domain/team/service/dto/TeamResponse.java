package com.teamnet.team_net.domain.team.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.time.LocalDateTime;

public class TeamResponse {

    @Getter
    @Builder
    public static class TeamResponseDto {
        Long id;
        String name;
        String teamImage;
        String createdBy;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Seoul")
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class TeamListResponseDto {
        PagedModel<TeamResponseDto> teams;
    }
}
