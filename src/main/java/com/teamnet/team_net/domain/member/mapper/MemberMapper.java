package com.teamnet.team_net.domain.member.mapper;

import com.teamnet.team_net.domain.member.dto.MemberResponse;
import com.teamnet.team_net.domain.member.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.domain.member.entity.Member;

public abstract class MemberMapper {
    public static UpdateMemberResponseDto toUpdateMemberResponseDto(Member member) {
        return UpdateMemberResponseDto.builder()
                .updateMemberId(member.getId())
                .nickname(member.getNickname())
                .build();
    }
}
