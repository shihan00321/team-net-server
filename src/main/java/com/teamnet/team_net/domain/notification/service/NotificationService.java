package com.teamnet.team_net.domain.notification.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.notification.entity.Notification;
import com.teamnet.team_net.domain.notification.mapper.NotificationMapper;
import com.teamnet.team_net.domain.notification.repository.NotificationRepository;
import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse;
import com.teamnet.team_net.domain.sse.EmitterRepository;
import com.teamnet.team_net.global.exception.handler.NotificationHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationMapper notificationMapper;

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private static final String NOTIFICATION_NAME = "notification";
    private static final String CONNECT_NAME = "connect";

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long memberId) {
        SseEmitter sseEmitter = createEmitter(memberId);
        sendConnectEvent(sseEmitter, memberId);
        return sseEmitter;
    }

    @Transactional
    public void sendTeamInvitation(Member sender, Member recipient, Long teamId) {
        // 팩토리 사용
        Notification notification = Notification.createTeamInvitation(sender, recipient, teamId);

        // 저장
        notificationRepository.save(notification);

        // 응답 DTO 변환
        NotificationResponse.NotificationResponseDto response = notificationMapper.toNotificationResponseDto(notification);

        // 알림 전송
        emitterRepository.get(recipient.getId())
                .ifPresentOrElse(
                        emitter -> sendNotificationEvent(emitter, recipient.getId(), response),
                        () -> log.info("No emitter found")
                );
    }

    @Transactional
    public void markNotificationsAsRead(Long memberId, List<Long> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) {
            return;
        }
        notificationRepository.markAsRead(notificationIds);
    }

    public NotificationResponse.NotificationListResponseDto findNotificationList(Long memberId) {
        List<Notification> notifications = notificationRepository.findNotifications(memberId);
        return notificationMapper.toNotificationResponseListDto(notifications, "test");
    }

    private void sendConnectEvent(SseEmitter emitter, Long memberId) {
        try {
            emitter.send(SseEmitter.event()
                    .name(CONNECT_NAME)
                    .data("connect completed"));
        } catch (IOException e) {
            emitterRepository.deleteById(memberId);
            throw new NotificationHandler(ErrorStatus.NOTIFICATION_CONNECT_ERROR);
        }
    }

    private SseEmitter createEmitter(Long memberId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, emitter);

        emitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        emitter.onTimeout(() -> emitterRepository.deleteById(memberId));

        return emitter;
    }

    private void sendNotificationEvent(SseEmitter emitter, Long memberId, NotificationResponse.NotificationResponseDto response) {
        try {
            emitter.send(SseEmitter.event()
                    .name(NOTIFICATION_NAME)
                    .data(response));
        } catch (IOException e) {
            emitterRepository.deleteById(memberId);
            throw new NotificationHandler(ErrorStatus.NOTIFICATION_CONNECT_ERROR);
        }
    }
}

