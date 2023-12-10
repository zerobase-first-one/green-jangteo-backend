package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.CategoryDetailDto;
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

    public void saveCategory(Long productId, List<CategoryDetailDto> categories) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));

        for (CategoryDetailDto category : categories) {
            categoryRepository.save(Category.of(product, category.getCategory(), category.getLevel()));
        }
    }

    public void updateCategory(Long productId, Product product, List<CategoryDetailDto> categories) {
        categoryRepository.deleteAllByProductId(productId);
        for (CategoryDetailDto category : categories) {
            categoryRepository.save(Category.of(product, category.getCategory(), category.getLevel()));
        }
    }
}
