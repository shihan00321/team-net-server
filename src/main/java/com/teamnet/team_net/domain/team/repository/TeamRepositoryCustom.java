package com.teamnet.team_net.domain.team.repository;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamSearchType;

public interface TeamRepositoryCustom {
    Team findTeamByKeyword(String keyword, TeamSearchType type);
}
