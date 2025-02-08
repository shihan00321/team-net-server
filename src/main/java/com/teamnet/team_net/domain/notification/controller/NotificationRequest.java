package com.teamnet.team_net.domain.notification.controller;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

public class NotificationRequest {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class NotificationReadRequest {
        @NotNull(message = "알림 ID 목록은 필수입니다.")
        List<Long> notificationIds;
    }
}