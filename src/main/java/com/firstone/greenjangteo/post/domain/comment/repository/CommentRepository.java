package com.firstone.greenjangteo.post.domain.comment.repository;

import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPostId(Long postId, Pageable pageable);

    Optional<Comment> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT COUNT(c) FROM comment c WHERE c.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);
}
