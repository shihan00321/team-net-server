package com.teamnet.team_net.global.config.auth;

import com.teamnet.team_net.domain.member.enums.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String redirectUrl = oAuth2User.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(Role.GUEST.getKey()))
                ? "http://localhost:5173/member-info"
                : "http://localhost:5173/home";
        response.sendRedirect(redirectUrl);
    }
}