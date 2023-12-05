package com.firstone.greenjangteo.product.repository;

import com.firstone.greenjangteo.product.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByProductId(Long productId);
    void deleteByProductId(Long productId);
}
