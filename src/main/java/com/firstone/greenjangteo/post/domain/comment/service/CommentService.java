package com.firstone.greenjangteo.post.domain.comment.service;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    Comment createComment(CommentRequestDto commentRequestDto);

    Page<Comment> getComments(Pageable pageable, Long postId);

    int getCommentCountForPost(Long postId);
}
