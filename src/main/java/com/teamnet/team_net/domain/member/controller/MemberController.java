package com.teamnet.team_net.domain.member.controller;

import com.teamnet.team_net.domain.member.service.MemberService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/members")
@RestController
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/additional")
    public ResponseEntity<Long> saveAdditionalInfo(
            @Valid @RequestBody MemberRequest.AdditionalMemberInfoDto memberInfoDto,
            @LoginMember SessionMember sessionMember,
            HttpServletRequest request, HttpServletResponse response) {
        Long memberId = memberService.saveAdditionalMemberInfo(request, response, memberInfoDto, sessionMember.getId());
        return ResponseEntity.ok(memberId);
    }
}
