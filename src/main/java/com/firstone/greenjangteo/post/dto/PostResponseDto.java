package com.firstone.greenjangteo.post.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.post.domain.image.dto.ImageResponseDto;
import com.firstone.greenjangteo.post.model.entity.Post;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDto {
    private Long postId;
    private Long userId;
    private String username;
    private String subject;
    private String content;
    private int viewCount;
    private int commentCount;
    private List<ImageResponseDto> imageResponseDtos;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    public static PostResponseDto from(Post post) {
        User user = post.getUser();

        return PostResponseDto.builder()
                .postId(post.getId())
                .userId(user.getId())
                .username(user.getUsername().getValue())
                .subject(post.getSubject())
                .content(post.getContent())
                .imageResponseDtos(
                        post.getImages() == null
                                ? null
                                : post.getImages().stream().map(ImageResponseDto::from)
                                .collect(Collectors.toList())
                )
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }

    public static PostResponseDto from(Post post, int viewCount, int commentCount) {
        User user = post.getUser();

        return PostResponseDto.builder()
                .postId(post.getId())
                .userId(user.getId())
                .username(user.getUsername().getValue())
                .subject(post.getSubject())
                .content(post.getContent())
                .viewCount(viewCount)
                .commentCount(commentCount)
                .imageResponseDtos(
                        post.getImages() == null
                                ? null
                                : post.getImages().stream().map(ImageResponseDto::from)
                                .collect(Collectors.toList())
                )
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
}
