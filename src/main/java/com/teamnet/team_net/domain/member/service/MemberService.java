package com.teamnet.team_net.domain.member.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.service.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.domain.member.service.dto.MemberServiceDTO;
import com.teamnet.team_net.domain.notification.entity.Notification;
import com.teamnet.team_net.domain.notification.mapper.NotificationMapper;
import com.teamnet.team_net.domain.notification.repository.NotificationRepository;
import com.teamnet.team_net.domain.notification.service.dto.NotificationResponse.NotificationListResponseDto;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.teamnet.team_net.domain.member.mapper.MemberMapper.toUpdateMemberResponseDto;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final SecurityContextManager securityContextManager;
    private final EntityChecker entityChecker;
    private final NotificationRepository notificationRepository;

    @Transactional
    public UpdateMemberResponseDto saveAdditionalMemberInfo(MemberServiceDTO.AdditionalMemberInfoServiceDTO memberInfoDto, Long memberId) {
        entityChecker.findMemberByNickname(memberInfoDto.getNickname());

        Member member = entityChecker.findMemberById(memberId);
        member.addNickname(memberInfoDto.getNickname());
        member.updateRole();
        securityContextManager.updateSecurityContext(member);
        return toUpdateMemberResponseDto(member);
    }

    public NotificationListResponseDto findNotificationList(Long memberId) {
        List<Notification> notifications = notificationRepository.findNotifications(memberId);
        return NotificationMapper.toNotificationResponseListDto(notifications);
    }
}
