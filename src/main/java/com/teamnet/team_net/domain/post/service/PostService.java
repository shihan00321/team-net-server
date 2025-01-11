package com.teamnet.team_net.domain.post.service;

import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.post.controller.PostRequest;
import com.teamnet.team_net.domain.post.dto.PostResponse;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.mapper.PostMapper;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.global.config.auth.LoginMember;
import com.teamnet.team_net.global.config.auth.dto.SessionMember;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.exception.handler.PostHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostResponse.PostResponseDto findOne(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));

        return PostMapper.toPostResponseDto(post);
    }

    public List<PostResponse.PostResponseDto> findAll() {
        return postRepository.findAll().stream()
                .map(post -> PostResponse.PostResponseDto.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Long save(Long memberId, PostRequest.PostSaveDto postSaveDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Post savedPost = postRepository.save(Post.builder()
                .title(postSaveDto.getTitle())
                .content(postSaveDto.getContent())
                .member(member)
                .build());
        return savedPost.getId();
    }

    @Transactional
    public Long update(Long memberId, Long postId, PostRequest.PostUpdateDto postUpdateDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new MemberHandler(ErrorStatus.POST_UNAUTHORIZED);
        }
        post.update(postUpdateDto.getTitle(), postUpdateDto.getContent());
        return postId;
    }

    @Transactional
    public Long delete(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(memberId)) {
            throw new MemberHandler(ErrorStatus.POST_UNAUTHORIZED);
        }

        postRepository.delete(post);
        return postId;
    }
}
