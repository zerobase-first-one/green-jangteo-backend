package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewsResponseDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.product.form.CreateReviewForm;
import com.firstone.greenjangteo.product.form.UpdateReviewForm;
import com.firstone.greenjangteo.product.repository.ReviewRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final UserService userService;

    public ReviewResponseDto saveReview(CreateReviewForm createReviewForm) {
        Product product = productService.getProduct(createReviewForm.getProductId());
        User user = userService.getUser(createReviewForm.getUserId());
        Review review = ReviewDto.createdOf(createReviewForm, user, product);
        Review createdReview = reviewRepository.save(review);
        return ReviewResponseDto.of(createdReview);
    }

    public List<ReviewsResponseDto> readAllReviewsForProducts(Long productId) {
        List<ReviewDto> reviews = reviewRepository.findAllByProduct(productId);
        List<ReviewsResponseDto> resultReviews = new ArrayList<>();
        for (ReviewDto review : reviews) {
            resultReviews.add(ReviewsResponseDto.of(review));
        }
        return resultReviews;
    }

    public List<ReviewsResponseDto> readAllReviewsForUsers(Long userId) {
        List<ReviewDto> reviews = reviewRepository.findAllByUser(userId);
        List<ReviewsResponseDto> resultReviews = new ArrayList<>();
        for (ReviewDto review : reviews) {
            resultReviews.add(ReviewsResponseDto.of(review));
        }
        return resultReviews;
    }

    public void updateReview(UpdateReviewForm updateReviewForm) {
        Optional<Review> review = reviewRepository.findById(updateReviewForm.getReviewId());
        ReviewDto reviewDto = ReviewDto.modifiedOf(updateReviewForm, review);
        reviewRepository.save(Review.of(reviewDto));
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        review.get().setUser(null);
        review.get().setProduct(null);
        reviewRepository.deleteById(reviewId);
    }
}
