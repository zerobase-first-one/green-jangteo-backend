package com.firstone.greenjangteo.product.repository;

import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<ReviewDto> findAllByProduct(Product product);

    List<ReviewDto> findAllByUser(User user);

    Optional<Review> findById(Long reviewId);
}
