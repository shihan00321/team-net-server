package com.teamnet.team_net.domain.team.controller;

import com.teamnet.team_net.domain.team.dto.TeamResponse;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ApiResponse<Long> createTeam(@LoginMember SessionMember sessionMember, @Valid @RequestBody TeamRequest.CreateTeamDto request) {
        return ApiResponse.onSuccess(teamService.createTeam(sessionMember.getId(), request));
    }

    @GetMapping
    public ApiResponse<List<TeamResponse.TeamResponseDto>> myTeam(@LoginMember SessionMember sessionMember) {
        return ApiResponse.onSuccess(teamService.findMyTeams(sessionMember.getId()));
    }
}
