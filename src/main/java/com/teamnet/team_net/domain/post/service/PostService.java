package com.teamnet.team_net.domain.post.service;

import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.mapper.PostMapper;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.post.service.dto.PostResponse;
import com.teamnet.team_net.domain.post.service.dto.PostServiceDTO;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.global.utils.checker.AuthorizationFacade;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.teamnet.team_net.domain.post.mapper.PostMapper.toPost;
import static com.teamnet.team_net.domain.post.mapper.PostMapper.toPostResponseDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final EntityChecker entityChecker;
    private final AuthorizationFacade authorizationFacade;

    public PostResponse.PostResponseDto findOne(Long postId) {
        Post post = entityChecker.findPostById(postId);
        return toPostResponseDto(post);
    }

    public PostResponse.PostListResponseDto findAllByTeamId(Long teamId) {
        List<Post> posts = postRepository.findAllByTeamId(teamId);
        return PostMapper.toPostListResponseDto(posts);
    }

    @Transactional
    public PostResponse.PostResponseDto save(Long memberId, Long teamId, PostServiceDTO.PostSaveServiceDTO postSaveDto) {
        TeamMember teamMember = entityChecker.findTeamMemberByMemberIdAndTeamId(memberId, teamId);
        Post savedPost = postRepository.save(toPost(postSaveDto, teamMember));
        return toPostResponseDto(savedPost);
    }

    @Transactional
    public PostResponse.PostResponseDto update(Long memberId, Long postId, PostServiceDTO.PostUpdateServiceDTO postUpdateDto) {
        Post post = entityChecker.findPostById(postId);
        authorizationFacade.validate("postAuthorizationChecker", memberId, post);
        post.update(postUpdateDto.getTitle(), postUpdateDto.getContent());
        return toPostResponseDto(post);
    }

    @Transactional
    public void delete(Long memberId, Long postId) {
        Post post = entityChecker.findPostById(postId);
        authorizationFacade.validate("postAuthorizationChecker", memberId, post);
        postRepository.delete(post);
    }
}
