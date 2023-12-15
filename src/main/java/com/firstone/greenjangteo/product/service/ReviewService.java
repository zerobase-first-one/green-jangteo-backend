package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewsResponseDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.product.exception.ErrorCode;
import com.firstone.greenjangteo.product.exception.ReviewException;
import com.firstone.greenjangteo.product.form.CreateReviewForm;
import com.firstone.greenjangteo.product.form.UpdateReviewForm;
import com.firstone.greenjangteo.product.repository.ReviewRepository;
import com.firstone.greenjangteo.user.model.entity.User;
import com.firstone.greenjangteo.user.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Review review = Review.createdOf(createReviewForm, user, product);
        Review createdReview = reviewRepository.save(review);
        return ReviewResponseDto.of(createdReview);
    }
    @Transactional(readOnly = true)
    public List<ReviewsResponseDto> readAllReviewsForProducts(Long productId) {
        Product product = productService.getProduct(productId);
        List<ReviewDto> reviews = reviewRepository.findAllByProduct(product);
        List<ReviewsResponseDto> resultReviews = reviews.stream().map(ReviewsResponseDto::of).collect(Collectors.toList());
        if (resultReviews.size() == 0) throw new ReviewException(ErrorCode.REVIEW_IS_NOT_FOUND);
        return resultReviews;
    }
    @Transactional(readOnly = true)
    public List<ReviewsResponseDto> readAllReviewsForUser(Long userId) {
        User user = userService.getUser(userId);
        List<ReviewDto> reviews = reviewRepository.findAllByUser(user);
        List<ReviewsResponseDto> resultReviews = reviews.stream().map(ReviewsResponseDto::of).collect(Collectors.toList());
        if (resultReviews.size() == 0) throw new ReviewException(ErrorCode.REVIEW_IS_NOT_FOUND);
        return resultReviews;
    }

    public void updateReview(UpdateReviewForm updateReviewForm) {
        Optional<Review> review = reviewRepository.findById(updateReviewForm.getReviewId());
        Review updateReview = Review.modifiedOf(updateReviewForm, review);
        reviewRepository.save(updateReview);
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> review = reviewRepository.findById(reviewId);
        Review.deleteOf(review);
        reviewRepository.deleteById(review.get().getId());
    }
}
