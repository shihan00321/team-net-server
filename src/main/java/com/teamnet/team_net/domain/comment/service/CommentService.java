package com.teamnet.team_net.domain.comment.service;

import com.teamnet.team_net.domain.comment.controller.CommentRequest;
import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.mapper.CommentMapper;
import com.teamnet.team_net.domain.comment.repository.CommentRepository;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.global.utils.checker.AuthorizationFacade;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        return toCommentResponseDTO(comment, null);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long memberId, Long commentId, CommentServiceDTO.UpdateCommentServiceDto request) {
        Comment comment = entityChecker.findCommentById(commentId);
        authorizationFacade.validate("commentAuthorizationChecker", memberId, comment);

        comment.update(request.getContent());
        return toCommentResponseDTO(comment, null);
    }

    @Transactional
    public void deleteComment(Long memberId, Long commentId) {
        Comment comment = entityChecker.findCommentById(commentId);
        authorizationFacade.validate("commentAuthorizationChecker", memberId, comment);

        commentRepository.delete(comment);
    }

    public CommentResponse.CommentListResponseDTO findComments(Long postId, Pageable pageable) {
        // 1. 부모 댓글 페이징 조회
        Page<Comment> parentCommentsPage = commentRepository.findParentCommentsByPostId(postId, pageable);

        // 2. 부모 댓글들의 ID 목록 추출
        List<Long> parentIds = parentCommentsPage.getContent().stream()
                .map(Comment::getId)
                .toList();

        // 3. 대댓글 조회
        List<Comment> children = parentIds.isEmpty() ?
                Collections.emptyList() :
                commentRepository.findChildrenByParentIds(parentIds);

        // 4. 대댓글을 부모 댓글별로 그룹화
        Map<Long, List<Comment>> childrenMap = children.stream()
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        Page<CommentResponseDTO> commentDtoPage = parentCommentsPage.map(parent ->
                CommentMapper.toCommentResponseDTO(parent, childrenMap.getOrDefault(parent.getId(), Collections.emptyList()))
        );
        return CommentMapper.toCommentListResponseDTO(commentDtoPage);
    }
}
