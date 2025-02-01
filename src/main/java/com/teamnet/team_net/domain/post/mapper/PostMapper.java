package com.teamnet.team_net.domain.post.mapper;

import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

public abstract class PostMapper {
    public static PostResponse.PostResponseDto toPostResponseDto(Post post) {
        return PostResponse.PostResponseDto
                .builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public static PostResponse.PostListResponseDto toPostListResponseDto(Page<Post> posts) {
        Page<PostResponse.PostResponseDto> pages = posts.map(PostMapper::toPostResponseDto);
        PagedModel<PostResponse.PostResponseDto> pagedModel = new PagedModel<>(pages);
        return PostResponse.PostListResponseDto.builder()
                .posts(pagedModel)
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
