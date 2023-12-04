package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.model.Category;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.exception.ErrorCode;
import com.firstone.greenjangteo.product.exception.ProductException;
import com.firstone.greenjangteo.product.repository.CategoryRepository;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public Long saveCategory(Long productId, List<String> category) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));

        for (int idx = 0; idx < category.size(); idx++) {
            Category curCategory = Category.builder()
                    .product(product)
                    .categoryName(category.get(idx))
                    .level(idx)
                    .build();
            categoryRepository.save(curCategory);
        }
        return product.getId();
    }

    public void updateCategory(Long productId, Product product, List<String> categoryList) {
        int categoryDepth = categoryRepository.findByProductId(productId).size();
        categoryRepository.deleteByProductId(productId);
        for (int idx = 0; idx < categoryDepth; idx++) {
            Category category = Category.builder()
                    .product(product)
                    .categoryName(categoryList.get(idx))
                    .level(idx)
                    .build();
            categoryRepository.save(category);
        }
    }
}
