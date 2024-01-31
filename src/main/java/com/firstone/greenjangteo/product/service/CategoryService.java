package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.CategoryDetailDto;
import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.model.Category;
import com.firstone.greenjangteo.product.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<CategoryDetailDto> findAllCategory(){
        return categoryRepository.findAll().stream().map(CategoryDetailDto::of).collect(Collectors.toList());
    }

    public CategoryDetailDto saveCategory(CategoryDto categoryDto) {
        return CategoryDetailDto.of(categoryRepository.save(Category.of(categoryDto)));
    }

    public void updateCategory(CategoryDetailDto categoryDetailDto) {
        Optional<Category> category = categoryRepository.findById(categoryDetailDto.getCategoryId());
        Category updateCategory = Category.builder()
                .id(category.get().getId())
                .firstCategory(categoryDetailDto.getFirstCategory())
                .secondCategory(categoryDetailDto.getSecondCategory())
                .build();
        categoryRepository.save(updateCategory);
    }

    public void deleteCategory(Long categoryId){
        categoryRepository.deleteById(categoryId);
    }
}
