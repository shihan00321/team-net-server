package com.teamnet.team_net.domain.notification;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.notification.entity.Notification;
import com.teamnet.team_net.domain.notification.enums.NotificationType;
import org.springframework.stereotype.Component;

@Component
public class NotificationGenerator {
    public Notification createTeamInvitation(Member sender, Member recipient, Long teamId) {
        return Notification.builder()
                .title("팀 초대 메시지")
                .message(sender.getNickname() + "님이 팀에 초대하였습니다.")
                .member(recipient)
                .referenceId(teamId)
                .type(NotificationType.TEAM_INVITATION)
                .isRead(false)
                .build();
    }
}
