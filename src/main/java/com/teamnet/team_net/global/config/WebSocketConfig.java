package com.teamnet.team_net.global.config;

import com.teamnet.team_net.global.config.auth.LoginMemberMessagingArgumentResolver;
import com.teamnet.team_net.global.config.ws.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.session.Session;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSocketSecurity
@EnableWebSocketMessageBroker  // 메시지 브로커 : 메시지 전송을 중개하는 역할
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<Session> {

    private final LoginMemberMessagingArgumentResolver loginMemberArgumentResolver;
    private final RedisTemplate<String, String> redisTemplate;

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
    public void configureStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-connect")
                .setAllowedOrigins("http://localhost:5173")
                .addInterceptors(httpSessionHandshakeInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.addDecoratorFactory(this::chatWebSocketHandler);
    }

    @Bean
    HttpSessionHandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HttpSessionHandshakeInterceptor();
    }

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        return messages
                .nullDestMatcher().authenticated() // 목적지가 없는 메시지 인증 필요
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated() // WebSocket에서 클라이언트가 연결할 때 보내는 CONNECT 메시지에 대해 사용자가 인증된 상태여야만 연결을 허용
                .simpDestMatchers("/publish/**").hasRole("USER") // 해당 경로로 보내는 메시지에 대해 USER 역할을 가진 사용자만 접근 가능하도록 설정
                .simpSubscribeDestMatchers("/user/**", "/subscribe/**").hasRole("USER") // 해당 경로를 구독하려면 USER 역할을 가진 사용자
                .anyMessage().denyAll() // 나머지 모두 거부
                .build();
    }

    @Bean("csrfChannelInterceptor")
    public ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {
        };
    }

    @Bean
    public WebSocketHandlerDecorator chatWebSocketHandler(WebSocketHandler chatWebSocketHandler) {
        return new ChatWebSocketHandler(chatWebSocketHandler, redisTemplate);
    }
}