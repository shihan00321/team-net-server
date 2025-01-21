package com.teamnet.team_net.domain.comment.service;

import com.teamnet.team_net.domain.comment.controller.CommentRequest;
import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.repository.CommentRepository;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.global.utils.checker.AuthorizationFacade;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.teamnet.team_net.domain.comment.service.dto.CommentResponse.CommentResponseDTO;
import static com.teamnet.team_net.domain.comment.mapper.CommentMapper.toComment;
import static com.teamnet.team_net.domain.comment.mapper.CommentMapper.toCommentResponseDTO;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final EntityChecker entityChecker;
    private final AuthorizationFacade authorizationFacade;

    @Transactional
    public CommentResponseDTO createComment(Long memberId, Long teamId, Long postId, CommentServiceDTO.CreateCommentServiceDto request) {
        authorizationFacade.validate("teamAuthorizationChecker", memberId, teamId);

        Post post = entityChecker.findPostById(postId);
        Comment parent = entityChecker.findParentCommentIfExists(request.getParentId());

        Comment comment = commentRepository.save(toComment(request, post, parent));
        return toCommentResponseDTO(comment);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long memberId, Long commentId, CommentServiceDTO.UpdateCommentServiceDto request) {
        Comment comment = entityChecker.findCommentById(commentId);
        authorizationFacade.validate("commentAuthorizationChecker", memberId, comment);

        comment.update(request.getContent());
        return toCommentResponseDTO(comment);
    }

    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        Comment comment = entityChecker.findCommentById(commentId);
        authorizationFacade.validate("commentAuthorizationChecker", memberId, comment);

        commentRepository.delete(comment);
    }
}
