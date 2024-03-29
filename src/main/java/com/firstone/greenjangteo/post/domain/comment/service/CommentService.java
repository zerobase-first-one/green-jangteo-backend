package com.firstone.greenjangteo.post.domain.comment.service;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Comment createComment(CommentRequestDto commentRequestDto);

    Page<Comment> getComments(Pageable pageable, Long postId);

    Comment getComment(Long commentId, Long writerId);

    Comment updateComment(Long commentId, CommentRequestDto commentRequestDto);

    void deleteComment(Long commentId, Long userId);

    int getCommentCountForPost(Long postId);
}
