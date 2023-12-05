package com.firstone.greenjangteo.product.repository;

import com.firstone.greenjangteo.product.domain.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
