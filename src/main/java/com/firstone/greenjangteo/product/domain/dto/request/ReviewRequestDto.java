package com.firstone.greenjangteo.product.domain.dto.request;

import com.firstone.greenjangteo.product.domain.model.Review;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ReviewRequestDto {
    private Long userId;
    private Long productId;
    private String content;
    private int score;
    private String imageUrl;

    public static ReviewRequestDto of(Review review){
        return ReviewRequestDto.builder()
                .userId(review.getUser().getId())
                .productId(review.getProduct().getId())
                .content(review.getContent())
                .score(review.getScore())
                .imageUrl(review.getImageUrl())
                .build();
    }
}
