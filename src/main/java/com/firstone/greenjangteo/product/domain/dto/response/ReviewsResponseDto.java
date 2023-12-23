package com.firstone.greenjangteo.product.domain.dto.response;

import com.firstone.greenjangteo.product.domain.model.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewsResponseDto {
    private Long productId;
    private Long userId;
    private Long reviewId;
    private String content;
    private int score;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ReviewsResponseDto from(Review review) {
        return ReviewsResponseDto.builder()
                .productId(review.getProduct().getId())
                .userId(review.getUser().getId())
                .reviewId(review.getId())
                .content(review.getContent())
                .score(review.getScore())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .modifiedAt(review.getModifiedAt())
                .build();
    }
}