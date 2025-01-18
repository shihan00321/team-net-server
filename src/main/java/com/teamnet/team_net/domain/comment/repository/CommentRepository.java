package com.teamnet.team_net.domain.comment.repository;

import com.teamnet.team_net.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
