package com.teamnet.team_net.domain.comment.mapper;

import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class CommentMapper {

    public static Comment toComment(CommentServiceDTO.CreateCommentServiceDto request, Post post, Comment parent) {
        return Comment.builder()
                .post(post)
                .parent(parent)
                .content(request.getContent())
                .build();
    }

    public static CommentResponse.CommentResponseDTO toCommentResponseDTO(Comment parent, List<Comment> children) {
        List<CommentResponse.CommentResponseDTO> childDtos = null;
        if (children != null) {
            childDtos = children.stream()
                    .map(child -> toChildCommentResponseDTO(null, child))
                    .toList();
        }

        return CommentResponse.CommentResponseDTO.builder()
                .commentId(parent.getId())
                .parentId(parent.getParent() != null ? parent.getParent().getId() : null)
                .content(parent.getContent())
                .createdBy(parent.getCreatedBy())
                .createdAt(parent.getCreatedAt())
                .childrenComment(childDtos)
                .build();
    }

    public static CommentResponse.CommentListResponseDTO toCommentListResponseDTO(Long memberId, Page<Comment> parentCommentsPage, Map<Long, List<Comment>> childrenMap) {
        Page<CommentResponse.CommentResponseDTO> commentDtoPage = parentCommentsPage.map(parent ->
                toCommentWithIsMineResponseDTO(memberId, parent, childrenMap.getOrDefault(parent.getId(), Collections.emptyList()))
        );
        PagedModel<CommentResponse.CommentResponseDTO> result = new PagedModel<>(commentDtoPage);

        return CommentResponse.CommentListResponseDTO.builder()
                .comments(result)
                .build();
    }

    private static CommentResponse.CommentResponseDTO toCommentWithIsMineResponseDTO(Long memberId, Comment parent, List<Comment> children) {
        List<CommentResponse.CommentResponseDTO> childDtos = null;
        if (children != null) {
            childDtos = children.stream()
                    .map(child -> toChildCommentResponseDTO(memberId, child))
                    .toList();
        }

        return CommentResponse.CommentResponseDTO.builder()
                .commentId(parent.getId())
                .parentId(parent.getParent() != null ? parent.getParent().getId() : null)
                .content(parent.getContent())
                .isMine(parent.getPost().getMember().getId().equals(memberId))
                .createdBy(parent.getCreatedBy())
                .createdAt(parent.getCreatedAt())
                .childrenComment(childDtos)
                .build();
    }

    private static CommentResponse.CommentResponseDTO toChildCommentResponseDTO(Long memberId, Comment child) {
        return CommentResponse.CommentResponseDTO.builder()
                .commentId(child.getId())
                .parentId(child.getParent() != null ? child.getParent().getId() : null) // null 체크 추가
                .content(child.getContent())
                .isMine(memberId.equals(child.getPost().getMember().getId()))
                .createdBy(child.getCreatedBy())
                .createdAt(child.getCreatedAt())
                .build();
    }
}

