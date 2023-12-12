package com.firstone.greenjangteo.product.domain.dto.response;

import com.firstone.greenjangteo.product.domain.model.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long reviewId;
    private LocalDateTime createdAt;

    public static ReviewResponseDto of(Review review) {
        return ReviewResponseDto.builder()
                .reviewId(review.getId())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
