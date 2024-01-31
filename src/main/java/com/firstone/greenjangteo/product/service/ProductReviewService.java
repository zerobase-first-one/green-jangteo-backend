package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.response.ReviewResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewsResponseDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.product.exception.ErrorCode;
import com.firstone.greenjangteo.product.exception.ProductRelatedException;
import com.firstone.greenjangteo.product.form.CreateReviewForm;
import com.firstone.greenjangteo.product.form.UpdateReviewForm;
import com.firstone.greenjangteo.product.repository.ProductReviewRepository;
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
public class ProductReviewService {
    private final ProductReviewRepository productReviewRepository;
    private final ProductService productService;
    private final UserService userService;

    public ReviewResponseDto saveReview(CreateReviewForm createReviewForm) {
        Product product = productService.getProduct(createReviewForm.getProductId());
        User user = userService.getUser(createReviewForm.getUserId());
        Review review = Review.createdOf(createReviewForm, user, product);
        Review createdReview = productReviewRepository.save(review);
        return ReviewResponseDto.of(createdReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewsResponseDto> readAllReviewsForProducts(Long productId) {
        Product product = productService.getProduct(productId);
        List<Review> reviews = productReviewRepository.findAllByProduct(product);
        List<ReviewsResponseDto> resultReviews = reviews.stream().map(ReviewsResponseDto::from).collect(Collectors.toList());
        if (resultReviews.size() == 0) throw new ProductRelatedException(ErrorCode.REVIEW_IS_NOT_FOUND.getDescription());
        return resultReviews;
    }

    @Transactional(readOnly = true)
    public List<ReviewsResponseDto> readAllReviewsForUser(Long userId) {
        User user = userService.getUser(userId);
        List<Review> reviews = productReviewRepository.findAllByUser(user);
        List<ReviewsResponseDto> resultReviews = reviews.stream().map(ReviewsResponseDto::from).collect(Collectors.toList());
        if (resultReviews.size() == 0) throw new ProductRelatedException(ErrorCode.REVIEW_IS_NOT_FOUND.getDescription());
        return resultReviews;
    }

    public void updateReview(UpdateReviewForm updateReviewForm) {
        Optional<Review> review = productReviewRepository.findById(updateReviewForm.getReviewId());
        Review updateReview = Review.modifiedOf(updateReviewForm, review);
        productReviewRepository.save(updateReview);
    }

    public void deleteReview(Long reviewId) {
        Optional<Review> review = productReviewRepository.findById(reviewId);
        Review.deleteOf(review);
        productReviewRepository.deleteById(review.get().getId());
    }
}
