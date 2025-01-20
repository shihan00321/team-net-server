package com.teamnet.team_net.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class MemberResponse {
    @Getter
    @Builder
    @AllArgsConstructor
    public static class UpdateMemberResponseDto {
        Long updateMemberId;
        String nickname;
    }
}
