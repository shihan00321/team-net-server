package com.teamnet.team_net.domain.member.mapper;

import com.teamnet.team_net.domain.member.service.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.domain.member.entity.Member;

public abstract class MemberMapper {
    public static UpdateMemberResponseDto toUpdateMemberResponseDto(Member member) {
        return UpdateMemberResponseDto.builder()
                .updateMemberId(member.getId())
                .nickname(member.getNickname())
                .build();
    }
}
