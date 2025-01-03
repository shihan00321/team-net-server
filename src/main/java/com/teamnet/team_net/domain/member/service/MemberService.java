package com.teamnet.team_net.domain.member.service;

import com.teamnet.team_net.domain.member.controller.MemberRequest;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final SecurityContextRepository securityContextRepository;

    @Transactional
    public Long saveAdditionalMemberInfo(HttpServletRequest request, HttpServletResponse response, MemberRequest.AdditionalMemberInfoDto memberInfoDto, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(IllegalStateException::new);
        member.addNickname(memberInfoDto.getNickname());
        member.updateRole();
        updateSecurity(request, response);
        return memberId;
    }

    private void updateSecurity(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;

            List<GrantedAuthority> authorities = Collections.singletonList(
                    new SimpleGrantedAuthority(Role.USER.getKey())
            );

            OAuth2AuthenticationToken updatedAuthToken = new OAuth2AuthenticationToken(
                    authToken.getPrincipal(),
                    authorities,
                    authToken.getAuthorizedClientRegistrationId()
            );

            SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(updatedAuthToken);

            // SecurityContextRepository를 통해 변경사항 저장
            securityContextRepository.saveContext(context, request, response);
        }
    }

}
