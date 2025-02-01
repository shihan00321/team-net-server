package com.teamnet.team_net.domain.team.mapper;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public abstract class TeamMapper {
    public static Team toTeam(TeamServiceDTO.CreateTeamServiceDTO request) {
        return Team.builder()
                .name(request.getName())
                .status(TeamActiveStatus.ACTIVE)
                .build();
    }

    public static TeamResponse.TeamResponseDto toTeamResponseDto(Team team) {
        return TeamResponse.TeamResponseDto.builder()
                .id(team.getId())
                .name(team.getName())
                .teamImage(null)
                .createdBy(team.getCreatedBy())
                .createdAt(team.getCreatedAt())
                .build();
    }

    public static TeamResponse.TeamListResponseDto toTeamListResponseDto(Page<Team> teams) {
        Page<TeamResponse.TeamResponseDto> page = teams.map(TeamMapper::toTeamResponseDto);
        PagedModel<TeamResponse.TeamResponseDto> pagedModel = new PagedModel<>(page);
        return TeamResponse.TeamListResponseDto.builder()
                .teams(pagedModel)
                .build();
    }
}
