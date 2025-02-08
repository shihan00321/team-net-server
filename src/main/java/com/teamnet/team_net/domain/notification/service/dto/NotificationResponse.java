package com.teamnet.team_net.domain.notification.service.dto;

import com.teamnet.team_net.domain.notification.enums.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class NotificationResponse {

    @Getter
    @Builder
    public static class NotificationResponseDto {
        Long id;
        Long referenceId;
        String title;
        String message;
        Boolean isRead;
        NotificationType type;
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class NotificationListResponseDto {
        List<NotificationResponseDto> notifications;
    }
}
