package com.firstone.greenjangteo.product.controller;

import com.firstone.greenjangteo.product.domain.dto.response.ReviewResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewsResponseDto;
import com.firstone.greenjangteo.product.form.CreateReviewForm;
import com.firstone.greenjangteo.product.form.UpdateReviewForm;
import com.firstone.greenjangteo.product.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping(value = "/reviews")
    public ResponseEntity<ReviewResponseDto> createReview(
            @RequestBody CreateReviewForm createReviewForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().body(reviewService.saveReview(createReviewForm));
    }

    @GetMapping(value = "/reviews/products/{productId}")
    public ResponseEntity<List<ReviewsResponseDto>> readReviewsAboutProducts(
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok().body(reviewService.readAllReviewsForProducts(productId));
    }

    @GetMapping(value = "/reviews/users/{userId}")
    public ResponseEntity<List<ReviewsResponseDto>> readReviewsAboutUsers(
            @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok().body(reviewService.readAllReviewsForUsers(userId));
    }

    @PutMapping(value = "/reviews/{reviewId}")
    public ResponseEntity productReview(
            @RequestBody UpdateReviewForm updateReviewForm
    ) {
        reviewService.updateReview(updateReviewForm);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/reviews/{reviewId}")
    public ResponseEntity reviewDelete(
            @PathVariable("reviewId") Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
