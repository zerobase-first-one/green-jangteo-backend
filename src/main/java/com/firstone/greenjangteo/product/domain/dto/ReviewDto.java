package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Product product;
    private User user;
    private String content;
    private int score;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ReviewDto of(Review review) {
        return ReviewDto.builder()
                .product(review.getProduct())
                .user(review.getUser())
                .content(review.getContent())
                .score(review.getScore())
                .imageUrl(review.getImageUrl())
                .createdAt(review.getCreatedAt())
                .modifiedAt(review.getModifiedAt())
                .build();
    }
}
