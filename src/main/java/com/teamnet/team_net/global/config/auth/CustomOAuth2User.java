package com.teamnet.team_net.global.config.auth;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {

    private final Long id;
    private final String nickname;
    private final String nameAttributeKey;

    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes, String nameAttributeKey, Long id, String nickname) {
        super(authorities, attributes, nameAttributeKey);
        this.id = id;
        this.nickname = nickname;
        this.nameAttributeKey = nameAttributeKey;
    }
}