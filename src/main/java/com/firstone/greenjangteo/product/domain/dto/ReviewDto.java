package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.product.form.CreateReviewForm;
import com.firstone.greenjangteo.product.form.UpdateReviewForm;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Optional;


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

    public static Review createdOf(CreateReviewForm createReviewForm, User user, Product product) {
        return Review.builder()
                .user(user)
                .product(product)
                .content(createReviewForm.getContent())
                .score(createReviewForm.getScore())
                .imageUrl(createReviewForm.getImageUrl())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static ReviewDto modifiedOf(UpdateReviewForm updateReviewForm, Optional<Review> review) {
        return ReviewDto.builder()
                .user(review.get().getUser())
                .product(review.get().getProduct())
                .content(updateReviewForm.getContent())
                .score(updateReviewForm.getScore())
                .imageUrl(updateReviewForm.getImageUrl())
                .createdAt(review.get().getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
