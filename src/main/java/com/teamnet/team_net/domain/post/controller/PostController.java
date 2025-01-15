package com.teamnet.team_net.domain.post.controller;

import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams/{teamId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<List<PostResponse.PostResponseDto>> findAll(@PathVariable("teamId") Long teamId) {
        return ApiResponse.onSuccess(postService.findAllByTeamId(teamId));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse.PostResponseDto> findOne(@PathVariable("teamId") Long teamId, @PathVariable("postId") Long postId) {
        return ApiResponse.onSuccess(postService.findOne(postId));
    }

    @PostMapping
    public ApiResponse<Long> save(@LoginMember SessionMember sessionMember, @PathVariable("teamId") Long teamId, @Valid @RequestBody PostRequest.PostSaveDto postSaveDto) {
        return ApiResponse.onSuccess(postService.save(sessionMember.getId(), teamId, postSaveDto));
    }

    @PatchMapping("/{postId}")
    public ApiResponse<Long> update(@LoginMember SessionMember sessionMember, @PathVariable("teamId") Long teamId, @PathVariable("postId") Long postId, @Valid @RequestBody PostRequest.PostUpdateDto postUpdateDto) {
        return ApiResponse.onSuccess(postService.update(sessionMember.getId(), postId, postUpdateDto));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Long> delete(@LoginMember SessionMember sessionMember, @PathVariable("teamId") Long teamId, @PathVariable("postId") Long postId) {
        return ApiResponse.onSuccess(postService.delete(sessionMember.getId(), postId));
    }
}
