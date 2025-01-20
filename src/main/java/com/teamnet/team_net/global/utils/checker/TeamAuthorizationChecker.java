package com.teamnet.team_net.global.utils.checker;

import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.exception.handler.TeamHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TeamAuthorizationChecker implements AuthorizationChecker<Long> {
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public void validate(Long memberId, Long teamId) {
        if (!teamMemberRepository.existsByMemberIdAndTeamId(memberId, teamId)) {
            throw new TeamHandler(ErrorStatus.TEAM_MEMBER_UNAUTHORIZED);
        }
    }
}
