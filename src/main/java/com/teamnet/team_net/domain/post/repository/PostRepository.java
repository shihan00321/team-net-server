package com.teamnet.team_net.domain.post.repository;

import com.teamnet.team_net.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p where p.team.id = :teamId")
    Page<Post> findAllByTeamId(@Param("teamId") Long teamId, Pageable pageable);
}
