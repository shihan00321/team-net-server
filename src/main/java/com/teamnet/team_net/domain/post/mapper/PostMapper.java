package com.teamnet.team_net.domain.post.mapper;

import com.teamnet.team_net.domain.comment.mapper.CommentMapper;
import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class PostMapper {
    public static PostResponse.PostResponseDto toPostResponseDto(Post post) {
        return PostResponse.PostResponseDto
                .builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(CommentMapper.toCommentListResponseDTO(
                        post.getComments() != null ? post.getComments() : Collections.emptyList()))
                .build();
    }


    public static PostResponse.PostListResponseDto toPostListResponseDto(List<Post> posts) {
        return PostResponse.PostListResponseDto.builder()
                .posts(posts
                        .stream()
                        .map(PostMapper::toPostResponseDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Post toPost(PostServiceDTO.PostSaveServiceDTO postSaveDto, TeamMember teamMember) {
        return Post.builder()
                .title(postSaveDto.getTitle())
                .team(teamMember.getTeam())
                .content(postSaveDto.getContent())
                .member(teamMember.getMember())
                .build();
    }
}
