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
                .orElseThrow(IllegalStateException::new);

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
                .orElseThrow(IllegalStateException::new);

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
                .orElseThrow(IllegalStateException::new);

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 게시글을 삭제할 권한이 없습니다.");
        }
        post.update(postUpdateDto.getTitle(), postUpdateDto.getContent());
        return postId;
    }

    @Transactional
    public Long delete(Long memberId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(IllegalStateException::new);

        if (!post.getMember().getId().equals(memberId)) {
            throw new IllegalArgumentException("해당 게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
        return postId;
    }
}
