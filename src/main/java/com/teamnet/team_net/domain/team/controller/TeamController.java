package com.teamnet.team_net.domain.team.controller;

import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.domain.team.service.dto.TeamResponse;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ApiResponse<TeamResponse.TeamResponseDto> createTeam(
            @LoginMember SessionMember sessionMember,
            @Valid @RequestBody TeamRequest.CreateTeamDTO request) {
        return ApiResponse.onSuccess(teamService.createTeam(sessionMember.getId(), request.toCreateTeamServiceDTO()));
    }

    @GetMapping
    public ApiResponse<TeamResponse.TeamListResponseDto> myTeam(
            @LoginMember SessionMember sessionMember,
            Pageable pageable) {
        return ApiResponse.onSuccess(teamService.findMyTeams(sessionMember.getId(), pageable));
    }

    @PostMapping("/{teamId}/invite")
    public ApiResponse<Void> inviteMember(
            @LoginMember SessionMember sessionMember,
            @Valid @RequestBody TeamRequest.InviteMemberDTO inviteMemberDto,
            @PathVariable Long teamId) {
        teamService.invite(sessionMember.getId(), teamId, inviteMemberDto.toInviteMemberServiceDTO());
        return ApiResponse.onSuccess(null);
    }

    @PatchMapping("/{teamId}")
    public ApiResponse<Void> deleteTeam(
            @LoginMember SessionMember sessionMember,
            @PathVariable("teamId") Long teamId) {
        teamService.deleteTeam(sessionMember.getId(), teamId);
        return ApiResponse.onSuccess(null);
    }

    @PostMapping("/{teamId}/accept")
    public ApiResponse<String> acceptInvitation(
            @LoginMember SessionMember sessionMember,
            @PathVariable Long teamId) {
        teamService.accept(sessionMember.getId(), teamId);
        return ApiResponse.onSuccess("Invitation accepted");
    }

    @PostMapping("/{teamId}/reject")
    public ApiResponse<String> rejectInvitation() {
        return ApiResponse.onSuccess("Invitation rejected");
    }

    @GetMapping("/search")
    public ApiResponse<TeamResponse.TeamResponseDto> searchTeam(
            @ModelAttribute TeamRequest.TeamSearchDTO searchDTO) {
        return ApiResponse.onSuccess(teamService.searchTeam(searchDTO.toTeamSearchServiceDTO()));
    }
}
