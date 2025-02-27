package com.teamnet.team_net.domain.member.mapper;

import com.teamnet.team_net.domain.member.service.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.domain.member.entity.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    @Mapping(source = "id", target = "updateMemberId")
    UpdateMemberResponseDto toUpdateMemberResponseDto(Member member);
}
