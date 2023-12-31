package com.firstone.greenjangteo.post.utility;

import com.firstone.greenjangteo.post.domain.image.dto.ImageRequestDto;
import com.firstone.greenjangteo.post.dto.PostRequestDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.user.model.entity.User;

import java.util.List;

public class PostTestObjectFactory {
    public static PostRequestDto createPostRequestDto
            (String userId, String subject, String content) {
        return PostRequestDto.builder()
                .userId(userId)
                .subject(subject)
                .content(content)
                .build();
    }

    public static PostRequestDto createPostRequestDto
            (String userId, String subject, String content, List<ImageRequestDto> imageRequestDtos) {
        return PostRequestDto.builder()
                .userId(userId)
                .subject(subject)
                .content(content)
                .imageRequestDtos(imageRequestDtos)
                .build();
    }

    public static Post createPost(Long id, String subject, String content, User user) {
        return Post.builder()
                .id(id)
                .subject(subject)
                .content(content)
                .user(user)
                .build();
    }

    public static Post createPost(String subject, String content, User user) {
        return Post.builder()
                .subject(subject)
                .content(content)
                .user(user)
                .build();
    }

    public static Post createPost(String subject, String content) {
        return Post.builder()
                .subject(subject)
                .content(content)
                .build();
    }
}
