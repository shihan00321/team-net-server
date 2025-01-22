package com.teamnet.team_net.domain.comment.mapper;

import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.post.entity.Post;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CommentMapper {

    public static Comment toComment(CommentServiceDTO.CreateCommentServiceDto request, Post post, Comment parent) {
        return Comment.builder()
                .post(post)
                .parent(parent)
                .content(request.getContent())
                .build();
    }

    public static CommentResponse.CommentResponseDTO toCommentResponseDTO(Comment parent, List<Comment> children) {
        return CommentResponse.CommentResponseDTO.builder()
                .commentId(parent.getId())
                .parentId(parent.getParent() != null ? parent.getParent().getId() : null)
                .content(parent.getContent())
                .createdAt(parent.getCreatedAt())
                .childrenComment(children != null ?
                        children.stream()
                                .map(CommentMapper::toChildCommentResponseDTO)
                                .collect(Collectors.toList())
                        : Collections.emptyList()) // null일 경우 빈 리스트 처리
                .build();
    }

    private static CommentResponse.CommentResponseDTO toChildCommentResponseDTO(Comment child) {
        return CommentResponse.CommentResponseDTO.builder()
                .commentId(child.getId())
                .parentId(child.getParent() != null ? child.getParent().getId() : null) // null 체크 추가
                .content(child.getContent())
                .createdAt(child.getCreatedAt())
                .childrenComment(Collections.emptyList()) // 대댓글의 대댓글은 빈 리스트
                .build();
    }

    public static CommentResponse.CommentListResponseDTO toCommentListResponseDTO(Page<CommentResponse.CommentResponseDTO> comments) {
        return CommentResponse.CommentListResponseDTO.builder()
                .comments(comments)
                .build();
    }
}

