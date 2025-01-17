package com.teamnet.team_net.domain.team.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class TeamResponse {

    @Getter
    @Builder
    public static class TeamResponseDto {
        Long id;
        String name;
        String teamImage;
        LocalDateTime createdAt;
    }
}
