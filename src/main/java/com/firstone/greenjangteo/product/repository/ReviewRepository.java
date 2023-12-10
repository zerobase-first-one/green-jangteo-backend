package com.firstone.greenjangteo.product.repository;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<ReviewDto> findAllByProducts(Long productId);

    void deleteByUsers(Long userId);
}
