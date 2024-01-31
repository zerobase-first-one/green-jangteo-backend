package com.firstone.greenjangteo.product.repository;

import com.firstone.greenjangteo.product.domain.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CategoryRepository extends JpaRepository<Category, Long> {
}
