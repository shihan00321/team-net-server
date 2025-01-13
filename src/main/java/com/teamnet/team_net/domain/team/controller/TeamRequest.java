package com.teamnet.team_net.domain.team.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamRequest {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateTeamDto {
        @NotBlank(message = "팀 이름은 비어있을 수 없습니다.")
        String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InviteMemberDto {
        @NotBlank(message = "초대하려는 멤버 이메일을 입력해주세요.")
        String email;
    }
}
