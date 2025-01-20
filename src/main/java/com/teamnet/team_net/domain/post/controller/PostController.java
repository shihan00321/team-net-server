package com.teamnet.team_net.domain.post.controller;

import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static com.teamnet.team_net.domain.post.dto.PostResponse.PostListResponseDto;
import static com.teamnet.team_net.domain.post.dto.PostResponse.PostResponseDto;

@RestController
@RequestMapping("/api/teams/{teamId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<PostListResponseDto> findAll(
            @PathVariable("teamId") Long teamId) {
        return ApiResponse.onSuccess(postService.findAllByTeamId(teamId));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponseDto> findOne(
            @PathVariable("postId") Long postId) {
        return ApiResponse.onSuccess(postService.findOne(postId));
    }

    @PostMapping
    public ApiResponse<PostResponseDto> save(
            @LoginMember SessionMember sessionMember,
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody PostRequest.PostSaveDto postSaveDto) {
        return ApiResponse.onSuccess(postService.save(sessionMember.getId(), teamId, postSaveDto));
    }

    @PatchMapping("/{postId}")
    public ApiResponse<PostResponseDto> update(
            @LoginMember SessionMember sessionMember,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequest.PostUpdateDto postUpdateDto) {
        return ApiResponse.onSuccess(postService.update(sessionMember.getId(), postId, postUpdateDto));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @LoginMember SessionMember sessionMember,
            @PathVariable("postId") Long postId) {
        postService.delete(sessionMember.getId(), postId);
        return ApiResponse.onSuccess(null);
    }
}