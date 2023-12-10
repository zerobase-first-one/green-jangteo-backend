package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.user.model.Username;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewDto {
    private Username username;
    private String content;
    private int score;

    private static ReviewDto of(Review review){
        return ReviewDto.builder()
                .username(review.getUsers().getUsername())
                .content(review.getContent())
                .score(review.getScore())
                .build();
    }
}
