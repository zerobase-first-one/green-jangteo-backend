package com.firstone.greenjangteo.product.controller;

import com.firstone.greenjangteo.product.domain.dto.ProductNameDto;
import com.firstone.greenjangteo.product.domain.dto.response.AddProductResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductDetailResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductsResponseDto;
import com.firstone.greenjangteo.product.domain.dto.search.ProductSaveAllRequest;
import com.firstone.greenjangteo.product.domain.dto.search.ProductSearchResponse;
import com.firstone.greenjangteo.product.form.AddProductForm;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import com.firstone.greenjangteo.product.service.ProductSearchService;
import com.firstone.greenjangteo.product.service.ProductService;
import org.springframework.data.domain.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductSearchService productSearchService;

    @PostMapping(value = "/product")
    public ResponseEntity<AddProductResponseDto> addProduct(
            @RequestBody AddProductForm addProductForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(addProductForm));
    }

    @PostMapping(value = "/products")
    public ResponseEntity<Void> addProducts(@RequestBody ProductSaveAllRequest productSaveAllRequest) {
        productSearchService.saveAllProducts(productSaveAllRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductsResponseDto>> productListAll(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductList(page, size));
    }

    @PostMapping("/productDocuments")
    public ResponseEntity<Void> saveProductDocument() {

        productSearchService.saveAllProductDocument();
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/products/category")
    public ResponseEntity<List<ProductSearchResponse>> searchByCategory(@RequestParam String category, Pageable pageable) {
        return ResponseEntity.ok(productSearchService.findByCategory(category, pageable));
    }

    @GetMapping("/products/keyword")
    public ResponseEntity<List<ProductSearchResponse>> searchByProductName(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(productSearchService.findByProductName(keyword, pageable));
    }

    @GetMapping("/products/auto-complete")
    public ResponseEntity<List<ProductNameDto>> findByStartWithProductName(@RequestParam String keyword) {
        return ResponseEntity.ok(productSearchService.findByStartWithProductName(keyword));
    }

    @GetMapping(value = "/products/{productId}/description")
    public ResponseEntity<ProductDetailResponseDto> productDescription(
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductDescription(productId));
    }

    @GetMapping(value = "/products/{productId}/review")
    public ResponseEntity<ProductDetailResponseDto> productReview(
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductReviews(productId));
    }

    @PutMapping(value = "/products/{productId}")
    public ResponseEntity<Void> productUpdate(
            @RequestBody UpdateProductForm updateProductForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        productService.updateProduct(updateProductForm);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping(value = "/products/{productId}")
    public ResponseEntity<Void> productRemove(
            @PathVariable("productId") Long productId
    ) {
        productService.removeProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
