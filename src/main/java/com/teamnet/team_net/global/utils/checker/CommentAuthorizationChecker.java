package com.teamnet.team_net.global.utils.checker;

import com.teamnet.team_net.domain.comment.entity.Comment;
import com.teamnet.team_net.domain.member.entity.Member;
import com.teamnet.team_net.domain.member.repository.MemberRepository;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Objects;

@RequiredArgsConstructor
@Component
public class CommentAuthorizationChecker implements AuthorizationChecker<Comment> {
    private final MemberRepository memberRepository;

    @Override
    public void validate(Long memberId, Comment comment) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (!Objects.equals(member.getNickname(), comment.getCreatedBy())) {
            throw new MemberHandler(ErrorStatus.COMMENT_UNAUTHORIZED);
        }
    }
}
