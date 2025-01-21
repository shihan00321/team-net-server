package com.teamnet.team_net.domain.team.controller;

import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
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
    public static class CreateTeamDTO {
        @NotBlank(message = "팀 이름은 비어있을 수 없습니다.")
        String name;

        protected TeamServiceDTO.CreateTeamServiceDTO toCreateTeamServiceDTO() {
            return TeamServiceDTO.CreateTeamServiceDTO.builder()
                    .name(name)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InviteMemberDTO {
        @NotBlank(message = "초대하려는 멤버 이메일을 입력해주세요.")
        String email;

        protected TeamServiceDTO.InviteMemberServiceDTO toInviteMemberServiceDTO() {
            return TeamServiceDTO.InviteMemberServiceDTO.builder()
                    .email(email)
                    .build();
        }
    }
}
