package com.teamnet.team_net.domain.member.service;

import com.teamnet.team_net.domain.member.controller.MemberRequest;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.notification.dto.NotificationResponse;
import com.teamnet.team_net.domain.notification.repository.NotificationRepository;
import com.teamnet.team_net.global.config.auth.CustomOAuth2User;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final SecurityContextRepository securityContextRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Long saveAdditionalMemberInfo(HttpServletRequest request, HttpServletResponse response, MemberRequest.AdditionalMemberInfoDto memberInfoDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        member.addNickname(memberInfoDto.getNickname());
        member.updateRole();
        updateSecurity(request, response, member);
        return memberId;
    }

    public List<NotificationResponse.NotificationResponseDto> findNotificationList(Long memberId) {
        return notificationRepository.findNotifications(memberId)
                .stream().map(notification -> NotificationResponse.NotificationResponseDto
                        .builder()
                        .id(notification.getId())
                        .title(notification.getTitle())
                        .message(notification.getMessage())
                        .createdAt(notification.getCreatedAt())
                        .build()).collect(Collectors.toList());
    }

    private void updateSecurity(HttpServletRequest request, HttpServletResponse response, Member member) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            CustomOAuth2User existingPrincipal = (CustomOAuth2User) authToken.getPrincipal();

            CustomOAuth2User updatedPrincipal = new CustomOAuth2User(
                    Collections.singleton(new SimpleGrantedAuthority(Role.USER.getKey())),
                    existingPrincipal.getAttributes(),
                    existingPrincipal.getNameAttributeKey(),
                    existingPrincipal.getId(),
                    member.getNickname()
            );

            OAuth2AuthenticationToken updatedAuthToken = new OAuth2AuthenticationToken(
                    updatedPrincipal,
                    updatedPrincipal.getAuthorities(),
                    authToken.getAuthorizedClientRegistrationId()
            );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(updatedAuthToken);

            securityContextRepository.saveContext(context, request, response);
        }
    }
}
