package com.teamnet.team_net.domain.comment.service;

import com.teamnet.team_net.domain.comment.controller.CommentRequest;
import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.repository.CommentRepository;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.exception.handler.CommentHandler;
import com.teamnet.team_net.global.exception.handler.PostHandler;
import com.teamnet.team_net.global.exception.handler.TeamHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Transactional
    public Long createComment(Long memberId, Long teamId, Long postId, CommentRequest.CreateCommentDto request) {
        TeamMember teamMember = teamMemberRepository.findByMemberIdAndTeamId(memberId, teamId)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_MEMBER_UNAUTHORIZED));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));

        Comment parent = null;
        if (request.getParentId() != null) {
            parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
        }

         return commentRepository.save(Comment.builder()
                .post(post)
                .parent(parent)
                .content(request.getContent())
                .build()).getId();
    }
}
