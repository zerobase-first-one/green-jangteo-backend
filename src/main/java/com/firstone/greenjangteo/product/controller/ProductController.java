package com.firstone.greenjangteo.product.controller;

import com.firstone.greenjangteo.product.domain.dto.response.AddProductResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductDetailResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductsResponseDto;
import com.firstone.greenjangteo.product.form.AddProductForm;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import com.firstone.greenjangteo.product.service.ProductService;
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

    @PostMapping(value = "/products")
    public ResponseEntity<AddProductResponseDto> addProduct(
            @RequestBody AddProductForm addProductForm,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(addProductForm));
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductsResponseDto>> productListAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductList());
    }

    @GetMapping(value = "/products/{productId}/description")
    public ResponseEntity<ProductDetailResponseDto> productDescription(
            @PathVariable("productId") Long productId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getProductDescription(productId));
    }

    @PutMapping(value = "/products/{productId}")
    public ResponseEntity productUpdate(
            @RequestBody UpdateProductForm updateProductForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        productService.updateProduct(updateProductForm);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @DeleteMapping(value = "/products/{productId}")
    public ResponseEntity productRemove(
            @PathVariable("productId") Long productId
    ) {
        productService.removeProduct(productId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
