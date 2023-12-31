package com.firstone.greenjangteo.post.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.post.model.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostsResponseDto {
    private Long postId;
    private String username;
    private String subject;
    private int viewCount;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    public static PostsResponseDto from(Post post, int viewCount) {
        return PostsResponseDto.builder()
                .postId(post.getId())
                .username(post.getUser().getUsername().getValue())
                .subject(post.getSubject())
                .viewCount(viewCount)
                .createdAt(post.getCreatedAt())
                .modifiedAt(post.getModifiedAt())
                .build();
    }
}
