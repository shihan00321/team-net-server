package com.teamnet.team_net.domain.notification.mapper;

import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse;
import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse.NotificationListResponseDto;
import com.teamnet.team_net.domain.notification.entity.Notification;

import java.util.List;
import java.util.stream.Collectors;

public abstract class NotificationMapper {
    public static NotificationResponse.NotificationResponseDto toNotificationResponseDto(Notification notification) {
        return NotificationResponse.NotificationResponseDto
                .builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .build();
    }

    public static NotificationListResponseDto toNotificationResponseListDto(List<Notification> notifications) {
        return NotificationListResponseDto.builder()
                .notifications(notifications.stream().map(NotificationMapper::toNotificationResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
