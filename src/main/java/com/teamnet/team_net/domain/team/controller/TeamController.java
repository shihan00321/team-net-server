package com.teamnet.team_net.domain.team.controller;

import com.teamnet.team_net.domain.team.dto.TeamResponse.TeamListResponseDto;
import com.teamnet.team_net.domain.team.dto.TeamResponse.TeamResponseDto;
import com.teamnet.team_net.domain.team.service.TeamService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.teamnet.team_net.domain.post.dto.PostResponse.PostListResponseDto;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public ApiResponse<TeamResponseDto> createTeam(
            @LoginMember SessionMember sessionMember,
            @Valid @RequestBody TeamRequest.CreateTeamDto request) {
        return ApiResponse.onSuccess(teamService.createTeam(sessionMember.getId(), request));
    }

    @GetMapping
    public ApiResponse<TeamListResponseDto> myTeam(
            @LoginMember SessionMember sessionMember) {
        return ApiResponse.onSuccess(teamService.findMyTeams(sessionMember.getId()));
    }

    @GetMapping("/{teamId}")
    public ApiResponse<PostListResponseDto> findTeamPosts(
            @LoginMember SessionMember sessionMember,
            @PathVariable("teamId") Long teamId) {
        return ApiResponse.onSuccess(teamService.findTeamPosts(sessionMember.getId(), teamId));
    }

    @PostMapping("/{teamId}/invite")
    public ApiResponse<Void> inviteMember(
            @LoginMember SessionMember sessionMember,
            @RequestBody TeamRequest.InviteMemberDto inviteMemberDto,
            @PathVariable Long teamId) {
        teamService.invite(sessionMember.getId(), teamId, inviteMemberDto);
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
}
