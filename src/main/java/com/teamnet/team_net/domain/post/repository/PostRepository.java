package com.teamnet.team_net.domain.post.repository;

import com.teamnet.team_net.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
