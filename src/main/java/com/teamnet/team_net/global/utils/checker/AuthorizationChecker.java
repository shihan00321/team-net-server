package com.teamnet.team_net.global.utils.checker;

public interface AuthorizationChecker<T> {
    void validate(Long memberId, T target);
}

