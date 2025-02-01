package com.teamnet.team_net.global.config;

import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.service.SecurityContextManager;
import com.teamnet.team_net.global.config.auth.CustomAccessDeniedHandler;
import com.teamnet.team_net.global.config.auth.CustomOAuth2UserService;
import com.teamnet.team_net.global.config.auth.OAuth2LoginSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOauth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(CsrfConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(HttpBasicConfigurer::disable);

        http.oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) ->
                                userInfoEndpointConfig.userService(customOauth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler))
                .exceptionHandling(handler -> handler
                        .accessDeniedHandler(customAccessDeniedHandler));

        http.authorizeHttpRequests((auth) -> auth
                .requestMatchers("/api/members/additional").hasAuthority(Role.GUEST.getKey())
                .requestMatchers("/api/posts/**", "/api/teams/**").hasAuthority(Role.USER.getKey())
                .requestMatchers("/", "/oauth2/**", "/login/**", "/docs/**").permitAll()
                .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityContextManager securityContextManager(
            SecurityContextRepository securityContextRepository,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        return new SecurityContextManager(securityContextRepository, request, response);
    }
}