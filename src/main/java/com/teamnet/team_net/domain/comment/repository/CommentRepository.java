package com.teamnet.team_net.domain.comment.repository;

import com.teamnet.team_net.domain.comment.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select c from Comment c where c.post.id = :postId and c.parent is null")
    Page<Comment> findParentCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    // 대댓글 조회
    @Query("select c from Comment c where c.parent.id in :parentIds")
    List<Comment> findChildrenByParentIds(@Param("parentIds") List<Long> parentIds);
}
