package com.teamnet.team_net.domain.member.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.mapper.MemberMapper;
import com.teamnet.team_net.domain.member.service.dto.MemberResponse.UpdateMemberResponseDto;
import com.teamnet.team_net.domain.member.service.dto.MemberServiceDTO;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final SecurityContextManager securityContextManager;
    private final EntityChecker entityChecker;
    private final MemberMapper memberMapper;
    @Transactional
    public UpdateMemberResponseDto saveAdditionalMemberInfo(MemberServiceDTO.AdditionalMemberInfoServiceDTO memberInfoDto, Long memberId) {
        entityChecker.findMemberByNickname(memberInfoDto.getNickname());

        Member member = entityChecker.findMemberById(memberId);
        member.addNickname(memberInfoDto.getNickname());
        member.updateRole();
        securityContextManager.updateSecurityContext(member);
        return memberMapper.toUpdateMemberResponseDto(member);
    }
}
