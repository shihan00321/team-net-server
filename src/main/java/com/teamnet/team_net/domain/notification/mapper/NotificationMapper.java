package com.teamnet.team_net.domain.notification.mapper;

import com.teamnet.team_net.domain.notification.entity.Notification;
import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse;
import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse.NotificationListResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse.NotificationResponseDto toNotificationResponseDto(Notification notification);

    default NotificationListResponseDto toNotificationResponseListDto(List<Notification> notifications, String test) {
        return NotificationListResponseDto.builder()
                .notifications(notifications.stream().map(this::toNotificationResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }
}
