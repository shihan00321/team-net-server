package com.teamnet.team_net.global.utils.checker;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
@Slf4j
public class AuthorizationFacade {

    // `AuthorizationChecker<?>`로 선언해서 다양한 타입을 지원하도록 변경
    private final Map<String, AuthorizationChecker<?>> checkerMap;

    @SuppressWarnings("unchecked")
    public <T> void validate(String checkerType, Long memberId, T target) {
        log.info("log : {}", checkerMap.keySet());
        AuthorizationChecker<T> checker = (AuthorizationChecker<T>) checkerMap.get(checkerType);
        if (checker == null) {
            throw new IllegalArgumentException("Checker not found for type: " + checkerType);
        }
        checker.validate(memberId, target);
    }
}

