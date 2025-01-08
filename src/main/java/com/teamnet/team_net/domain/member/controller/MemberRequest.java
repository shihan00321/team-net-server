package com.teamnet.team_net.domain.member.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AdditionalMemberInfoDto {
        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        String nickname;
    }
}
