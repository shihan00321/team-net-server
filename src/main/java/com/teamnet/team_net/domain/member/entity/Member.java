package com.teamnet.team_net.domain.member.entity;

import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.global.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickname;
    private String email;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    private DeletionStatus status;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Member update(String name, String email) {
        this.name = name;
        this.email = email;
        return this;
    }

    public void addNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateRole() {
        this.role = Role.USER;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
