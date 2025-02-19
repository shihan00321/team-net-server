package com.teamnet.team_net.global.config.auth;

import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import org.springframework.core.MethodParameter;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.support.MessageMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Component
public class LoginMemberMessagingArgumentResolver extends MessageMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginMemberAnnotation = parameter.getParameterAnnotation(LoginMember.class) != null;
        boolean isMemberClass = SessionMember.class.equals(parameter.getParameterType());
        return isLoginMemberAnnotation && isMemberClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, Message<?> message) {
        // WebSocket에서 사용자 정보 가져오기
        SimpMessageHeaderAccessor accessor = SimpMessageHeaderAccessor.wrap(message);
        return accessor.getSessionAttributes().get("member"); // WebSocket 세션에서 사용자 정보 가져오기
    }
}
