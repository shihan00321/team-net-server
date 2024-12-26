package com.teamnet.team_net.domain.member.entity;

import com.teamnet.team_net.common.entity.BaseTimeEntity;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
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
}
