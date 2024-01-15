package com.firstone.greenjangteo.post.domain.comment.repository;

import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
