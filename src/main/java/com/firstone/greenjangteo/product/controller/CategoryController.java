package com.firstone.greenjangteo.product.controller;

import com.firstone.greenjangteo.product.domain.dto.CategoryDetailDto;
import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping(value ="/categories")
    public ResponseEntity<List<CategoryDetailDto>> getCategories() {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.findAllCategory());
    }

    @PostMapping(value = "/category")
    public ResponseEntity<CategoryDetailDto> createCategory(
            @RequestBody CategoryDto categoryDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(categoryDto));
    }
    @PutMapping(value = "/category")
    public ResponseEntity<Void> updateCategory(
            @RequestBody CategoryDetailDto categoryDetailDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        categoryService.updateCategory(categoryDetailDto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping(value = "/category")
    public ResponseEntity<Void> deleteCategory(
            @RequestParam Long categoryId
    ){
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
