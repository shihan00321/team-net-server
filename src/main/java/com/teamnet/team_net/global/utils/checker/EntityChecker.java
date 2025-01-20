package com.teamnet.team_net.global.utils.checker;

import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.comment.repository.CommentRepository;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.repository.PostRepository;
import com.teamnet.team_net.domain.team.entity.Team;
import com.teamnet.team_net.domain.team.repository.TeamRepository;
import com.teamnet.team_net.domain.teammember.entity.TeamMember;
import com.teamnet.team_net.domain.teammember.enums.TeamRole;
import com.teamnet.team_net.domain.teammember.repository.TeamMemberRepository;
import com.teamnet.team_net.global.exception.handler.CommentHandler;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.exception.handler.PostHandler;
import com.teamnet.team_net.global.exception.handler.TeamHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EntityChecker {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;

    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
    }

    public Comment findCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
    }

    public Comment findParentCommentIfExists(Long parentId) {
        if (parentId == null) {
            return null;
        }
        return findCommentById(parentId);
    }

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    public Team findTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_NOT_FOUND));
    }

    public TeamMember findTeamMemberByMemberIdAndTeamId(Long memberId, Long teamId) {
        return teamMemberRepository.findByMemberIdAndTeamId(memberId, teamId)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_MEMBER_NOT_FOUND));
    }

    public TeamMember findTeamMemberByMemberIdAndTeamIdAndRole(Long memberId, Long teamId, TeamRole role) {
        return teamMemberRepository.findByMemberIdAndTeamIdAndRole(memberId, teamId, TeamRole.ADMIN)
                .orElseThrow(() -> new TeamHandler(ErrorStatus.TEAM_MEMBER_UNAUTHORIZED));
    }
}

