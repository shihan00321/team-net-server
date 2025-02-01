package com.teamnet.team_net.domain.comment;

import com.teamnet.team_net.domain.IntegrationTestSupport;
import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.repository.CommentRepository;
import com.teamnet.team_net.domain.comment.service.CommentService;
import com.teamnet.team_net.domain.comment.service.dto.CommentResponse;
import com.teamnet.team_net.domain.comment.service.dto.CommentServiceDTO;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.enums.DeletionStatus;
import com.teamnet.team_net.domain.member.enums.Role;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.enums.TeamActiveStatus;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.config.auth.CustomOAuth2User;
import com.teamnet.team_net.global.exception.handler.CommentHandler;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.exception.handler.PostHandler;
import com.teamnet.team_net.global.exception.handler.TeamHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommentService commentService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private TeamMemberRepository teamMemberRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private TeamRepository teamRepository;

    private Member member;
    private Team team;
    private Post post;
    private Comment parentComment;

    @BeforeEach
    void setUp() {
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")),
                Map.of("id", 1L, "nickname", "testUser", "sub", "1234567890"),
                "sub",
                1L,
                "testUser"
        );

        Authentication authentication = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        member = memberRepository.save(Member.builder()
                .name("testName")
                .email("xxx.xxx.com")
                .role(Role.USER)
                .status(DeletionStatus.NOT_DELETE)
                .nickname("testUser")
                .build());

        team = teamRepository.save(Team.builder()
                .name("testTeam")
                .status(TeamActiveStatus.ACTIVE)
                .build());

        teamMemberRepository.save(TeamMember.builder()
                .member(member)
                .role(TeamRole.ADMIN)
                .team(team)
                .build());

        post = postRepository.save(Post.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .member(member)
                .team(team)
                .build());

        parentComment = commentRepository.save(Comment.builder()
                .content("부모 댓글")
                .post(post)
                .build());
    }
    @Test
    @DisplayName("댓글 생성 테스트")
    void createComment() {
        CommentServiceDTO.CreateCommentServiceDto request = CommentServiceDTO.CreateCommentServiceDto.builder()
                .content("테스트 댓글")
                .build();

        CommentResponse.CommentResponseDTO response = commentService.createComment(member.getId(), team.getId(), post.getId(), request);

        Comment savedComment = commentRepository.findById(response.getCommentId())
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        assertThat(savedComment.getContent()).isEqualTo("테스트 댓글");
        assertThat(savedComment.getPost().getId()).isEqualTo(post.getId());
        assertThat(savedComment.getParent()).isNull();
    }

    @Test
    @DisplayName("대댓글 생성 테스트")
    void createReplyComment() {
        CommentServiceDTO.CreateCommentServiceDto request = CommentServiceDTO.CreateCommentServiceDto.builder()
                .content("테스트 대댓글")
                .parentId(parentComment.getId())
                .build();

        CommentResponse.CommentResponseDTO response = commentService.createComment(member.getId(), team.getId(), post.getId(), request);

        Comment findComment = commentRepository.findById(response.getCommentId())
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        assertThat(findComment.getContent()).isEqualTo("테스트 대댓글");
        assertThat(findComment.getParent().getId()).isEqualTo(parentComment.getId());
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("댓글 수정 테스트")
    void updateComment() {
        Comment comment = commentRepository.save(Comment.builder()
                .content("원본 댓글")
                .post(post)
                .build());

        CommentServiceDTO.UpdateCommentServiceDto request = CommentServiceDTO.UpdateCommentServiceDto.builder()
                .content("수정된 댓글")
                .build();

        CommentResponse.CommentResponseDTO updatedComment = commentService.updateComment(member.getId(), comment.getId(), request);

        Comment findComment = commentRepository.findById(updatedComment.getCommentId())
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));

        assertThat(findComment.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @WithMockUser(username = "testUser")
    @DisplayName("댓글 삭제 테스트")
    void deleteComment() {
        Comment comment = commentRepository.save(Comment.builder()
                .content("삭제할 댓글")
                .post(post)
                .build());

        commentService.deleteComment(member.getId(), comment.getId());
        assertThat(commentRepository.findById(comment.getId())).isEmpty();
    }

    @Test
    @DisplayName("권한 없는 사용자의 댓글 수정 시 예외 발생")
    void updateCommentUnauthorized() {
        Comment comment = commentRepository.save(Comment.builder()
                .content("원본 댓글")
                .post(post)
                .build());

        Member unauthorizedMember = memberRepository.save(Member.builder()
                .name("unauthorizedMember")
                .status(DeletionStatus.NOT_DELETE)
                .email("bbb.bbb.com")
                .role(Role.USER)
                .nickname("unauthorizedMember")
                .build());

        CommentServiceDTO.UpdateCommentServiceDto request = CommentServiceDTO.UpdateCommentServiceDto.builder()
                .content("수정된 댓글")
                .build();

        assertThrows(MemberHandler.class, () ->
                commentService.updateComment(unauthorizedMember.getId(), comment.getId(), request)
        );
    }

    @Test
    @DisplayName("팀 멤버가 아닌 사용자의 댓글 생성 시 예외 발생")
    void createCommentUnauthorized() {
        Member nonTeamMember = memberRepository.save(Member.builder()
                .name("nonMemberName")
                .status(DeletionStatus.NOT_DELETE)
                .email("bbb.bbb.com")
                .role(Role.USER)
                .nickname("nonTeamMember")
                .build());

        CommentServiceDTO.CreateCommentServiceDto request = CommentServiceDTO.CreateCommentServiceDto.builder()
                .content("테스트 댓글")
                .build();

        assertThrows(TeamHandler.class, () ->
                commentService.createComment(nonTeamMember.getId(), team.getId(), post.getId(), request)
        );
    }

    @Test
    @DisplayName("존재하지 않는 게시글에 댓글 생성 시 예외 발생")
    void createCommentToNonExistentPost() {
        CommentServiceDTO.CreateCommentServiceDto request = CommentServiceDTO.CreateCommentServiceDto.builder()
                .content("테스트 댓글")
                .build();

        assertThrows(PostHandler.class, () ->
                commentService.createComment(member.getId(), team.getId(), 999L, request)
        );
    }
}