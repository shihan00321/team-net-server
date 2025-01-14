package com.teamnet.team_net.domain.notification.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.notification.dto.NotificationResponse;
import com.teamnet.team_net.domain.notification.entity.Notification;
import com.teamnet.team_net.domain.notification.enums.NotificationType;
import com.teamnet.team_net.domain.notification.repository.NotificationRepository;
import com.teamnet.team_net.domain.sse.EmitterRepository;
import com.teamnet.team_net.global.exception.handler.NotificationHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간
    private final static String ALARM_NAME = "alarm";
    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long memberId) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(memberId, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.deleteById(memberId));
        sseEmitter.onTimeout(() -> emitterRepository.deleteById(memberId));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect completed"));
        } catch (IOException e) {
            emitterRepository.deleteById(memberId);
            throw new NotificationHandler(ErrorStatus.NOTIFICATION_CONNECT_ERROR);
        }

        return sseEmitter;
    }

    public void send(Member member, Member targetMember, Long teamId) {
        Notification notification = notificationRepository.save(Notification.builder()
                .title("팀 초대 메시지")
                .message(member.getNickname() + "님이 팀에 초대하였습니다.")
                .member(targetMember)
                .referenceId(teamId)
                .type(NotificationType.TEAM_INVITATION)
                .isRead(false)
                .build());

        emitterRepository.get(member.getId())
                .ifPresentOrElse(sseEmitter -> {
                    try {
                        sseEmitter.send(SseEmitter.event()
                                .id("")
                                .name(ALARM_NAME)
                                .data(NotificationResponse.NotificationResponseDto.builder()
                                        .id(notification.getId())
                                        .title(notification.getTitle())
                                        .message(notification.getMessage())
                                        .createdAt(notification.getCreatedAt())
                                        .build()));
                    } catch (IOException e) {
                        emitterRepository.deleteById(member.getId());
                        throw new NotificationHandler(ErrorStatus.NOTIFICATION_CONNECT_ERROR);
                    }
                }, () -> log.info("No emitter founded"));
    }
}
