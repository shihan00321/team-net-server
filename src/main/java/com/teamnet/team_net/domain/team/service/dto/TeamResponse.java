package com.teamnet.team_net.domain.team.service.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

public class TeamResponse {

    @Getter
    @Builder
    public static class TeamResponseDto {
        Long id;
        String name;
        String teamImage;
        String createdBy;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class TeamListResponseDto {
        Page<TeamResponseDto> teams;
    }
}
