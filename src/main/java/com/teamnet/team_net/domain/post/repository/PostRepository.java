package com.teamnet.team_net.domain.post.repository;

import com.teamnet.team_net.domain.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p where p.team.id = :teamId")
    List<Post> findAllByTeamId(@Param("teamId") Long teamId);
}
