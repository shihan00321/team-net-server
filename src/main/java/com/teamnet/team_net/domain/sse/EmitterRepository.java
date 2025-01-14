package com.teamnet.team_net.domain.sse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Repository
public class EmitterRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Long emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    public void deleteById(Long emitterId) {
        emitters.remove(emitterId);
    }

    public Optional<SseEmitter> get(Long emitterId) {
        return Optional.ofNullable(emitters.get(emitterId));
    }
}
