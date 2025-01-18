package com.teamnet.team_net.domain.comment.controller;

import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/teams/{teamId}/posts/{postId}/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ApiResponse<Long> createComment(@LoginMember SessionMember sessionMember, @PathVariable("teamId") Long teamId, @PathVariable("postId") Long postId, @RequestBody CommentRequest.CreateCommentDto request) {
        return ApiResponse.onSuccess(commentService.createComment(sessionMember.getId(), teamId, postId, request));
    }

    @DeleteMapping("/{commentId}")
    public ApiResponse<Long> deleteComment(@LoginMember SessionMember sessionMember, @PathVariable("teamId") Long teamId, @PathVariable("postId") Long postId, @PathVariable("commentId") Long commentId) {
        return ApiResponse.onSuccess(commentService.deleteComment(sessionMember.getId(), commentId));
    }

}
