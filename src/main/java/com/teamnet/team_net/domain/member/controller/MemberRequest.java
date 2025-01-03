package com.teamnet.team_net.domain.member.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class MemberRequest {

    @Getter
    public static class AdditionalMemberInfoDto {
        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        String nickname;
    }
}
