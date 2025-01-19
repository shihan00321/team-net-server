package com.teamnet.team_net.domain.comment.mapper;

import com.teamnet.team_net.domain.comment.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.entity.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class CommentMapper {
    public static List<CommentResponse.CommentResponseDTO> toCommentListResponseDTO(List<Comment> comments) {
        // 최상위 댓글을 담을 리스트
        List<CommentResponse.CommentResponseDTO> topLevelComments = new ArrayList<>();

        // 댓글 ID와 DTO를 매핑하기 위한 Map
        Map<Long, CommentResponse.CommentResponseDTO> commentMap = new HashMap<>();

        for (Comment comment : comments) {
            // 현재 댓글을 DTO로 변환
            //computeIfAbsent: Map에 키가 없으면 생성하고 저장, 있으면 기존 값을 반환.
            CommentResponse.CommentResponseDTO currentDto = commentMap.computeIfAbsent(
                    comment.getId(),
                    id -> CommentResponse.CommentResponseDTO.builder()
                            .commentId(comment.getId())
                            .parentId(comment.getParent() != null ? comment.getParent().getId() : null)
                            .content(comment.getContent())
                            .createdAt(comment.getCreatedAt())
                            .childrenComment(new ArrayList<>())
                            .build()
            );

            // 부모 댓글이 없으면 최상위 댓글 리스트에 추가
            if (comment.getParent() == null) {
                topLevelComments.add(currentDto);
            } else {
                // 부모 댓글의 childrenComment에 현재 댓글 추가
                commentMap.computeIfAbsent(
                        comment.getParent().getId(),
                        parentId -> CommentResponse.CommentResponseDTO.builder()
                                .commentId(parentId)
                                .childrenComment(new ArrayList<>())
                                .build()
                ).getChildrenComment().add(currentDto);
            }
        }
        return topLevelComments;
    }
}

