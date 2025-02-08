package com.teamnet.team_net.domain.notification.controller;

import com.teamnet.team_net.domain.notification.service.NotificationService;
import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RequestMapping("/api")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public ApiResponse<NotificationResponse.NotificationListResponseDto> alarm(
            @LoginMember SessionMember sessionMember) {
        return ApiResponse.onSuccess(notificationService.findNotificationList(sessionMember.getId()));
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @LoginMember SessionMember sessionMember) {
        return notificationService.subscribe(sessionMember.getId());
    }

    @PostMapping("/notifications/read")
    public ApiResponse<Void> readNotifications(
            @Valid @RequestBody NotificationRequest.NotificationReadRequest request,
            @LoginMember SessionMember sessionMember
    ) {
        notificationService.markNotificationsAsRead(sessionMember.getId(), request.getNotificationIds());
        return ApiResponse.onSuccess(null);
    }
}
