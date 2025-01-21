package com.teamnet.team_net.domain.team.mapper;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;

import java.util.List;
import java.util.stream.Collectors;

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
                .createdAt(team.getCreatedAt())
                .build();
    }

    public static TeamResponse.TeamListResponseDto toTeamListResponseDto(List<Team> teams) {
        return TeamResponse.TeamListResponseDto.builder()
                .teams(teams.stream().map(TeamMapper::toTeamResponseDto).collect(Collectors.toList()))
                .build();
    }
}
