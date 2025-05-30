package com.teamnet.team_net.domain.team.service.dto;

import com.teamnet.team_net.domain.team.enums.TeamSearchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TeamServiceDTO {
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateTeamServiceDTO {
        String name;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InviteMemberServiceDTO {
        String email;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamSearchServiceDTO {
        String keyword;
        TeamSearchType type;
    }
}
