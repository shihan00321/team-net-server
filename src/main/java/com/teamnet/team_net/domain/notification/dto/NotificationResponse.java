package com.teamnet.team_net.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class NotificationResponse {

    @Getter
    @Builder
    public static class NotificationResponseDto {
        Long id;
        String title;
        String message;
        LocalDateTime createdAt;
    }
}
