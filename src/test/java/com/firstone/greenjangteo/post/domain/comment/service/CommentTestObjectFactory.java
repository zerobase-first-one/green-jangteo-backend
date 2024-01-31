package com.firstone.greenjangteo.post.domain.comment.service;

import com.firstone.greenjangteo.post.domain.comment.dto.CommentRequestDto;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.domain.image.model.entity.Image;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.user.model.entity.User;

import java.util.List;

public class CommentTestObjectFactory {
    public static CommentRequestDto createCommentRequestDto
            (String userId, String postId, String content, List<ImageRequestDto> imageRequestDtos) {
        return CommentRequestDto.builder()
                .userId(userId)
                .postId(postId)
                .content(content)
                .imageRequestDtos(imageRequestDtos)
                .build();
    }

    public static Comment createComment(String content) {
        return Comment.builder()
                .content(content)
                .build();
    }

    public static Comment createComment(String content, User user) {
        return Comment.builder()
                .content(content)
                .user(user)
                .build();
    }

    public static Comment createComment(String content, Post post) {
        return Comment.builder()
                .content(content)
                .post(post)
                .build();
    }

    public static Comment createComment(String content, User user, Post post) {
        return Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .build();
    }

    public static Comment createComment(String content, User user, Post post, List<Image> images) {
        return Comment.builder()
                .content(content)
                .user(user)
                .post(post)
                .images(images)
                .build();
    }

    public static Comment createComment(String commentId, String content, User user, Post post) {
        return Comment.builder()
                .id(Long.parseLong(commentId))
                .content(content)
                .user(user)
                .post(post)
                .build();
    }

    public static Comment createComment(String commentId, String content, User user, Post post, List<Image> images) {
        return Comment.builder()
                .id(Long.parseLong(commentId))
                .content(content)
                .user(user)
                .post(post)
                .images(images)
                .build();
    }
}
