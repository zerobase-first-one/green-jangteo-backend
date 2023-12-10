package com.firstone.greenjangteo.product.controller;

import com.firstone.greenjangteo.product.domain.dto.response.AddProductResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductDetailResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductsResponseDto;
import com.firstone.greenjangteo.product.form.AddProductForm;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import com.firstone.greenjangteo.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping(value = "/products")
    public ResponseEntity<AddProductResponseDto> addProduct(
            @RequestBody AddProductForm addProductForm,
            BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(productService.saveProduct(addProductForm));
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductsResponseDto>> productListAll() {
        return ResponseEntity.ok().body(productService.getProductList());
    }

    @GetMapping(value = "/products/{productId}/description")
    public ResponseEntity<ProductDetailResponseDto> productDescription(
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok().body(productService.getProductDescription(productId));
    }

    @GetMapping(value = "/products/{productId}/review")
    public ResponseEntity<ProductDetailResponseDto> productReview(
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.ok().body(productService.getProductReviews(productId));
    }

    @PutMapping(value = "/products/{productId}")
    public ResponseEntity productUpdate(
            @RequestBody UpdateProductForm updateProductForm,
            BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.notFound().build();
        }
        productService.updateProduct(updateProductForm);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping(value = "/products/{productId}")
    public ResponseEntity productRemove(
            @PathVariable("productId") Long productId
    ) {
        productService.removeProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
