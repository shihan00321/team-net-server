package com.teamnet.team_net.domain.member.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.global.config.auth.CustomOAuth2User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import java.util.Collections;

@RequiredArgsConstructor
@Component
public class SecurityContextManager {
    private final SecurityContextRepository securityContextRepository;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public void updateSecurityContext(Member member) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            context.setAuthentication(createUpdatedAuthToken((OAuth2AuthenticationToken) authentication, member));
            securityContextRepository.saveContext(context, request, response);
        }
    }

    private OAuth2AuthenticationToken createUpdatedAuthToken(OAuth2AuthenticationToken existingToken, Member member) {
        CustomOAuth2User existingPrincipal = (CustomOAuth2User) existingToken.getPrincipal();
        CustomOAuth2User updatedPrincipal = createUpdatedPrincipal(existingPrincipal, member);

        return new OAuth2AuthenticationToken(
                updatedPrincipal,
                updatedPrincipal.getAuthorities(),
                existingToken.getAuthorizedClientRegistrationId()
        );
    }

    private CustomOAuth2User createUpdatedPrincipal(CustomOAuth2User existingPrincipal, Member member) {
        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(Role.USER.getKey())),
                existingPrincipal.getAttributes(),
                existingPrincipal.getNameAttributeKey(),
                existingPrincipal.getId(),
                member.getNickname()
        );
    }
}
