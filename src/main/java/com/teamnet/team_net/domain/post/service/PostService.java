package com.teamnet.team_net.domain.post.service;

import com.teamnet.team_net.domain.post.dto.PostResponse.PostResponseDto;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.mapper.PostMapper;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.global.utils.checker.AuthorizationFacade;
import com.teamnet.team_net.global.utils.checker.EntityChecker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.teamnet.team_net.domain.post.controller.PostRequest.PostSaveDto;
import static com.teamnet.team_net.domain.post.controller.PostRequest.PostUpdateDto;
import static com.teamnet.team_net.domain.post.dto.PostResponse.PostListResponseDto;
import static com.teamnet.team_net.domain.post.mapper.PostMapper.toPost;
import static com.teamnet.team_net.domain.post.mapper.PostMapper.toPostResponseDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final EntityChecker entityChecker;
    private final AuthorizationFacade authorizationFacade;

    public PostResponseDto findOne(Long postId) {
        Post post = entityChecker.findPostById(postId);
        return toPostResponseDto(post);
    }

    public PostListResponseDto findAllByTeamId(Long teamId) {
        List<Post> posts = postRepository.findAllByTeamId(teamId);
        return PostMapper.toPostListResponseDto(posts);
    }

    @Transactional
    public PostResponseDto save(Long memberId, Long teamId, PostSaveDto postSaveDto) {
        TeamMember teamMember = entityChecker.findByMemberIdAndTeamId(memberId, teamId);
        Post savedPost = postRepository.save(toPost(postSaveDto, teamMember));
        return toPostResponseDto(savedPost);
    }

    @Transactional
    public PostResponseDto update(Long memberId, Long postId, PostUpdateDto postUpdateDto) {
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
