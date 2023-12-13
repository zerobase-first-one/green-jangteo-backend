package com.firstone.greenjangteo.product.repository;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<ReviewDto> findAllByProduct(Long productId);

    List<ReviewDto> findAllByUser(Long userId);

    Optional<Review> findById(Long reviewId);

    void deleteByUser(Long userId);
}
