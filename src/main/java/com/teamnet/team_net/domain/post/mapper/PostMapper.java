package com.teamnet.team_net.domain.post.mapper;

import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedModel;

@Mapper(componentModel = "spring") // MapStruct가 인터페이스에 대한 구현체를 컴파일 타임에 자동 생성.
public interface PostMapper { // 인터페이스로 변경

    @Mapping(target = "isMine", ignore = true)
    PostResponse.PostResponseDto toPostResponseDto(Post post);

    default PostResponse.PostListResponseDto toPostListResponseDto(Page<Post> posts, Long memberId) {
        Page<PostResponse.PostResponseDto> pages = posts.map(post -> toPostResponseDtoWithIsMine(
                post,
                memberId.equals(post.getMember().getId())
        ));
        PagedModel<PostResponse.PostResponseDto> pagedModel = new PagedModel<>(pages);
        return PostResponse.PostListResponseDto.builder()
                .posts(pagedModel)
                .build();
    }

    @Mapping(source = "teamMember.team", target = "team")
    @Mapping(source = "teamMember.member", target = "member")
    @Mapping(target = "id", ignore = true)
    Post toPost(PostServiceDTO.PostSaveServiceDTO postSaveDto, TeamMember teamMember);

    PostResponse.PostResponseDto toPostResponseDtoWithIsMine(Post post, Boolean isMine);
}
