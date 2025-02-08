package com.teamnet.team_net.domain.member.controller;

import com.teamnet.team_net.domain.member.service.MemberService;
import com.teamnet.team_net.domain.member.service.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<UpdateMemberResponseDto> saveAdditionalInfo(
            @Valid @RequestBody MemberRequest.AdditionalMemberInfoDTO memberInfoDto,
            @LoginMember SessionMember sessionMember) {
        return ApiResponse.onSuccess(memberService.saveAdditionalMemberInfo(memberInfoDto.toAdditionalMemberInfoServiceDTO(), sessionMember.getId()));
    }
}
