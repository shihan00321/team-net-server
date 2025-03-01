package com.teamnet.team_net.domain.comment.mapper;

import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.post.entity.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "member", target = "member")
    @Mapping(source = "post", target = "post")
    @Mapping(source = "parent", target = "parent")
    @Mapping(target = "content", source = "request.content")
    Comment toComment(CommentServiceDTO.CreateCommentServiceDto request, Post post, Comment parent, Member member);

    default CommentResponse.CommentListResponseDTO toCommentListResponseDTO(Long memberId, Page<Comment> parentCommentsPage, Map<Long, List<Comment>> childrenMap) {
        Page<CommentResponse.CommentResponseDTO> commentDtoPage = parentCommentsPage.map(parent ->
                toCommentResponseDTO(memberId, parent, childrenMap.getOrDefault(parent.getId(), Collections.emptyList()))
        );
        PagedModel<CommentResponse.CommentResponseDTO> result = new PagedModel<>(commentDtoPage);

        return CommentResponse.CommentListResponseDTO.builder()
                .comments(result)
                .build();
    }

    default CommentResponse.CommentResponseDTO toCommentResponseDTO(Long memberId, Comment parent, List<Comment> children) {
        return CommentResponse.CommentResponseDTO.builder()
                .commentId(parent.getId())
                .parentId(getParentId(parent))
                .content(parent.getContent())
                .isMine(getIsMine(memberId, parent.getMember().getId()))
                .createdBy(parent.getCreatedBy())
                .createdAt(parent.getCreatedAt())
                .childrenComment(mapChildrenComments(memberId, children))
                .build();
    }

    default List<CommentResponse.CommentResponseDTO> mapChildrenComments(Long memberId, List<Comment> children) {
        return (children == null) ? Collections.emptyList() :
                children.stream()
                        .map(child -> toCommentResponseDTO(memberId, child, Collections.emptyList())) // 여기서 memberId 추가
                        .toList();
    }

    default Long getParentId(Comment comment) {
        return (comment.getParent() != null) ? comment.getParent().getId() : null;
    }

    default boolean getIsMine(Long memberId, Long commentMemberId) {
        return memberId != null && memberId.equals(commentMemberId);
    }
}
