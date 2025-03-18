package com.teamnet.team_net.global.config.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.WebSocketHandlerDecorator;

import java.util.Objects;

@Slf4j
public class ChatWebSocketHandler extends WebSocketHandlerDecorator {

    private final RedisTemplate<String, String> redisTemplate;
    private final String redisKeyPrefix = "team:sessions:";

    public ChatWebSocketHandler(@Qualifier("chatWebSocketHandler") WebSocketHandler delegate, RedisTemplate<String, String> redisTemplate) {
        super(delegate);
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("WebSocket Connected: {}", session.getId());
        String teamId = extractTeamId(session);

        String redisKey = redisKeyPrefix + teamId;
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        setOperations.add(redisKey, Objects.requireNonNull(session.getPrincipal()).getName());
        super.afterConnectionEstablished(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        super.handleMessage(session, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        log.info("WebSocket Disconnected: {}", session.getId());
        String teamId = extractTeamId(session);

        String redisKey = redisKeyPrefix + teamId;
        SetOperations<String, String> setOperations = redisTemplate.opsForSet();

        // 팀 세션 리스트에서 제거
        setOperations.remove(redisKey, session.getId());

        super.afterConnectionClosed(session, status);
    }

    private String extractTeamId(WebSocketSession session) {
        String query = Objects.requireNonNull(session.getUri()).getQuery();
        return query.split("=")[1];
    }
}