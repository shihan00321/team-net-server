package com.teamnet.team_net.domain.team.mapper;

import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.dto.TeamServiceDTO;
import org.mapstruct.*;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(target = "status", constant = "ACTIVE")
    @Mapping(target = "id", ignore = true)
    Team toTeam(TeamServiceDTO.CreateTeamServiceDTO request);

    @Mapping(target = "teamImage", ignore = true)
    TeamResponse.TeamResponseDto toTeamResponseDto(Team team);

    default TeamResponse.TeamListResponseDto toTeamListResponseDto(Page<Team> teams) {
        Page<TeamResponse.TeamResponseDto> page = teams.map(this::toTeamResponseDto);
        PagedModel<TeamResponse.TeamResponseDto> pagedModel = new PagedModel<>(page);
        return TeamResponse.TeamListResponseDto.builder()
                .teams(pagedModel)
                .build();
    }

}
