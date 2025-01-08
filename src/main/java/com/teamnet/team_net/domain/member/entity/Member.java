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
    @Column(name = "member_id")
    private Long id;
    @Column(length = 20, nullable = false)
    private String name;
    @Column(length = 20, unique = true)
    private String nickname;
    @Column(length = 50, nullable = false)
    private String email;
    private String profileImage;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeletionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
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
