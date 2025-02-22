package com.teamnet.team_net.global.config;

import com.teamnet.team_net.global.config.auth.LoginMemberMessagingArgumentResolver;
import com.teamnet.team_net.global.config.ws.AuthenticationInterceptor;
import com.teamnet.team_net.global.config.ws.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;

@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Configuration
@EnableWebSocketMessageBroker  // 메시지 브로커 : 메시지 전송을 중개하는 역할
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final LoginMemberMessagingArgumentResolver loginMemberArgumentResolver;
    private final AuthenticationInterceptor authenticationInterceptor;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 스프링의 인메모리 메시지 브로커를 사용한다는 설정
        // 구독 주소의 prefix = /subscribe
        registry.enableSimpleBroker("/subscribe");

        // 메시지를 publish 하는 주소의 Prefix
        registry.setApplicationDestinationPrefixes("/publish");
        registry.setUserDestinationPrefix("/user");
    }

    /**
     * 오프닝 핸드셰이크 과정에서 사용할 엔드포인트 지정
     * ws://localhost:8080/ws-connect // 만약 https를 사용한다면 wss
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect")
                .setAllowedOrigins("http://localhost:5173")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .withSockJS();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authenticationInterceptor);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory((webSocketHandler) -> new WebSocketHandlerDecorator(webSocketHandler) {
            @Override
            public void afterConnectionEstablished(WebSocketSession session) throws Exception {
                chatWebSocketHandler.afterConnectionEstablished(session);
                super.afterConnectionEstablished(session);
            }

            @Override
            public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
                chatWebSocketHandler.afterConnectionClosed(session, closeStatus);
                super.afterConnectionClosed(session, closeStatus);
            }
        });
    }
}
