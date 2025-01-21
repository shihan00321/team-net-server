package com.teamnet.team_net.global.utils.checker;

import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.global.exception.handler.MemberHandler;
import com.teamnet.team_net.global.response.code.status.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class PostAuthorizationChecker implements AuthorizationChecker<Post> {

    @Override
    public void validate(Long memberId, Post post) {
        if (!post.getMember().getId().equals(memberId)) {
            throw new MemberHandler(ErrorStatus.POST_UNAUTHORIZED);
        }
    }
}
