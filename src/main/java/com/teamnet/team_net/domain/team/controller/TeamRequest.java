package com.teamnet.team_net.domain.team.controller;

import com.teamnet.team_net.domain.team.enums.TeamSearchType;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

public class TeamRequest {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
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
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor
    public static class InviteMemberDTO {
        @NotBlank(message = "초대하려는 멤버 이메일을 입력해주세요.")
        String email;

        protected TeamServiceDTO.InviteMemberServiceDTO toInviteMemberServiceDTO() {
            return TeamServiceDTO.InviteMemberServiceDTO.builder()
                    .email(email)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class TeamSearchDTO {
        @NotBlank(message = "검색하려는 팀 이름을 입력해주세요.")
        String keyword;

        @NotNull(message = "검색 타입을 선택해주세요.")
        TeamSearchType type;

        protected TeamServiceDTO.TeamSearchServiceDTO toTeamSearchServiceDTO() {
            return TeamServiceDTO.TeamSearchServiceDTO.builder()
                    .keyword(keyword)
                    .type(type)
                    .build();
        }
    }
}
