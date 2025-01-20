package com.teamnet.team_net.domain.comment.controller;

import com.teamnet.team_net.domain.comment.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.dto.CommentResponse.CommentResponseDTO;
import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams/{teamId}/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ApiResponse<CommentResponseDTO> createComment(
            @LoginMember SessionMember sessionMember,
            @PathVariable("teamId") Long teamId,
            @PathVariable("postId") Long postId,
            @Valid @RequestBody CommentRequest.CreateCommentDto request) {
        return ApiResponse.onSuccess(commentService.createComment(sessionMember.getId(), teamId, postId, request));
    }

    @PatchMapping("/{commentId}")
    public ApiResponse<CommentResponseDTO> updateComment(
            @LoginMember SessionMember sessionMember,
            @PathVariable("commentId") Long commentId,
            @Valid @RequestBody CommentRequest.CreateCommentDto request) {
        return ApiResponse.onSuccess(commentService.updateComment(sessionMember.getId(), commentId, request));
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @LoginMember SessionMember sessionMember,
            @PathVariable("commentId") Long commentId) {
        commentService.deleteComment(sessionMember.getId(), commentId);
        return ApiResponse.onSuccess(null);
    }

}
