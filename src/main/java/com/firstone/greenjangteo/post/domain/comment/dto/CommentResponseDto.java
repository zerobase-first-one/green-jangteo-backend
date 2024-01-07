package com.firstone.greenjangteo.post.domain.comment.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.firstone.greenjangteo.post.domain.comment.model.entity.Comment;
import com.firstone.greenjangteo.post.domain.image.dto.ImageResponseDto;
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
public class CommentResponseDto {
    private Long commentId;
    private Long userId;
    private String username;
    private String content;
    private List<ImageResponseDto> imageResponseDtos;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modifiedAt;

    public static CommentResponseDto from(Comment comment) {
        User user = comment.getUser();

        return CommentResponseDto.builder()
                .commentId(comment.getId())
                .userId(user.getId())
                .username(user.getUsername().getValue())
                .content(comment.getContent())
                .imageResponseDtos(
                        comment.getImages() == null
                                ? null
                                : comment.getImages().stream().map(ImageResponseDto::from)
                                .collect(Collectors.toList())
                )
                .createdAt(comment.getCreatedAt())
                .modifiedAt(comment.getModifiedAt())
                .build();
    }
}
