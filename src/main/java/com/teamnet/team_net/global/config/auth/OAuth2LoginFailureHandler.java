package com.teamnet.team_net.global.config.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().write("소셜 로그인 실패!");
        // 로그에 추가 정보 기록
        log.info("소셜 로그인에 실패했습니다. : {}", exception.getMessage());
        log.error("소셜 로그인 실패 정보: ", exception); // 스택 트레이스를 포함한 오류 로그
        log.info("요청 URI: {}", request.getRequestURI());
        log.info("요청 파라미터: {}", request.getParameterMap());
        log.info("헤더 정보: {}", extractHeaders(request));
    }

    private String extractHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append("\n");
        });
        return headers.toString();
    }
}
