package com.teamnet.team_net.domain.post.controller;

import com.teamnet.team_net.domain.post.service.PostService;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teams/{teamId}/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping
    public ApiResponse<PostResponse.PostListResponseDto> findAll(
            @LoginMember SessionMember sessionMember,
            @PathVariable("teamId") Long teamId,
            @ModelAttribute PostRequest.PostSearchKeywordDTO searchDTO,
            Pageable pageable) {
        System.out.println("searchDTO : " + searchDTO.keyword);
        System.out.println("searchDTO : " + searchDTO.type);
        return ApiResponse.onSuccess(postService.findAll(sessionMember.getId(), teamId, searchDTO.toPostSearchKeywordServiceDTO(), pageable));
    }

    @GetMapping("/{postId}")
    public ApiResponse<PostResponse.PostResponseDto> findOne(
            @PathVariable("postId") Long postId) {
        return ApiResponse.onSuccess(postService.findOne(postId));
    }

    @PostMapping
    public ApiResponse<PostResponse.PostResponseDto> save(
            @LoginMember SessionMember sessionMember,
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody PostRequest.PostSaveDTO postSaveDto) {
        return ApiResponse.onSuccess(postService.save(sessionMember.getId(), teamId, postSaveDto.toPostSaveServiceDTO()));
    }

    @PatchMapping("/{postId}")
    public ApiResponse<PostResponse.PostResponseDto> update(
            @LoginMember SessionMember sessionMember,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody PostRequest.PostUpdateDTO postUpdateDto) {
        return ApiResponse.onSuccess(postService.update(sessionMember.getId(), postId, postUpdateDto.toPostUpdateServiceDTO()));
    }

    @DeleteMapping("/{postId}")
    public ApiResponse<Void> delete(
            @LoginMember SessionMember sessionMember,
            @PathVariable("postId") Long postId) {
        postService.delete(sessionMember.getId(), postId);
        return ApiResponse.onSuccess(null);
    }
}