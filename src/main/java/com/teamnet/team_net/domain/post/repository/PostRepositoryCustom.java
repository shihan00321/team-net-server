package com.teamnet.team_net.domain.post.repository;

import com.teamnet.team_net.domain.post.entity.Post;
import com.teamnet.team_net.domain.post.enums.SearchType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {
    Page<Post> searchPosts(Long teamId, String keyword, SearchType type, Pageable pageable);
}
