package com.firstone.greenjangteo.product.controller;

import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import com.firstone.greenjangteo.product.domain.dto.ProductListDto;
import com.firstone.greenjangteo.product.service.CategoryService;
import com.firstone.greenjangteo.product.service.ProductService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @PostMapping(value = "/products")
    public ResponseEntity<Map<String, Object>> addProduct(
            ProductDto productDto,
            @RequestParam String productImageLocation,
            @RequestParam @ApiParam(example = "1") List<String> productImageList,
            @RequestParam @ApiParam(example = "가전제품") List<String> categoryList,
            BindingResult bindingResult
    ) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.notFound().build();
        }
        Map<String, Object> productInfo = productService.saveProduct(productDto, productImageList, productImageLocation);
        categoryService.saveCategory(Long.parseLong(productInfo.get("productId").toString()), categoryList);
        return ResponseEntity.ok().body(productInfo);
    }

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductListDto>> productListAll() {
        List<ProductListDto> productList = productService.getProductList();
        return ResponseEntity.ok().body(productList);
    }

    @GetMapping(value = "/products/{productId}")
    public ResponseEntity<ProductListDto> productDetail(
            @PathVariable("productId") Long productId
    ) {
        ProductListDto productDetail = productService.getProductDetail(productId);
        return ResponseEntity.ok().body(productDetail);
    }

    @PutMapping(value = "/products/{productId}")
    public ResponseEntity<Object> productUpdate(
            @PathVariable("productId") @ApiParam(example = "1") Long productId,
            @ApiParam(example = "") ProductDto productDto,
            @RequestParam @ApiParam(example = "C:/greenjangteo/product") String productImageLocation,
            @RequestParam @ApiParam(example = "1.jpg") List<String> productImageList,
            @RequestParam @ApiParam(example = "가전제품 2") List<String> categoryList,
            BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.notFound().build();
        }
        productService.updateProduct(productId, productDto, productImageList, categoryList, productImageLocation);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping(value = "/products/{productId}")
    public ResponseEntity<Object> productRemove(
            @PathVariable("productId") @ApiParam(example = "1") Long productId
    ) {
        productService.removeProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
