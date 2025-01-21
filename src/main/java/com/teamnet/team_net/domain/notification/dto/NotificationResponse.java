package com.teamnet.team_net.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {

    @Getter
    @Builder
    public static class NotificationResponseDto {
        Long id;
        String title;
        String message;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class NotificationListResponseDto {
        List<NotificationResponseDto> notifications;
    }
}
