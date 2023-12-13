package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.user.model.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

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
}
