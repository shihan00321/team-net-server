package com.teamnet.team_net.domain.notification.entity;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.notification.enums.NotificationType;
import com.teamnet.team_net.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "reference_id", nullable = false)
    private Long referenceId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(nullable = false)
    private Boolean isRead;

    public static Notification createTeamInvitation(Member sender, Member recipient, Long teamId) {
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
