package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.form.CreateReviewForm;
import com.firstone.greenjangteo.product.form.UpdateReviewForm;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "score", nullable = false)
    private int score;

    @Column(name = "imageUrl")
    private String imageUrl;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    public static Review of(ReviewDto reviewDto) {
        return Review.builder()
                .user(reviewDto.getUser())
                .product(reviewDto.getProduct())
                .content(reviewDto.getContent())
                .score(reviewDto.getScore())
                .imageUrl(reviewDto.getImageUrl())
                .createdAt(reviewDto.getCreatedAt())
                .modifiedAt(reviewDto.getModifiedAt())
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

    public static Review modifiedOf(UpdateReviewForm updateReviewForm, Optional<Review> review) {
        return Review.builder()
                .id(updateReviewForm.getReviewId())
                .user(review.get().getUser())
                .product(review.get().getProduct())
                .content(updateReviewForm.getContent())
                .score(updateReviewForm.getScore())
                .imageUrl(updateReviewForm.getImageUrl())
                .createdAt(review.get().getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static void deleteOf(Optional<Review> review){
        review.get().setUser(null);
        review.get().setProduct(null);
    }
}
