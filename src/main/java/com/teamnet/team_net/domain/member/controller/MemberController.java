package com.teamnet.team_net.domain.member.controller;

import com.teamnet.team_net.domain.member.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.domain.member.service.MemberService;
import com.teamnet.team_net.domain.notification.service.NotificationService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import static com.teamnet.team_net.domain.member.controller.MemberRequest.AdditionalMemberInfoDto;
import static com.teamnet.team_net.domain.notification.dto.NotificationResponse.NotificationListResponseDto;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final NotificationService notificationService;

    @PostMapping("/additional")
    public ApiResponse<UpdateMemberResponseDto> saveAdditionalInfo(
            @Valid @RequestBody AdditionalMemberInfoDto memberInfoDto,
            @LoginMember SessionMember sessionMember) {
        return ApiResponse.onSuccess(memberService.saveAdditionalMemberInfo(memberInfoDto, sessionMember.getId()));
    }

    @GetMapping("/notification")
    public ApiResponse<NotificationListResponseDto> alarm(
            @LoginMember SessionMember sessionMember) {
        return ApiResponse.onSuccess(memberService.findNotificationList(sessionMember.getId()));
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @LoginMember SessionMember sessionMember) {
        return notificationService.subscribe(sessionMember.getId());
    }
}
