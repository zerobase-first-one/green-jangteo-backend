package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import com.firstone.greenjangteo.product.domain.dto.ProductListDto;
import com.firstone.greenjangteo.product.domain.model.Category;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import com.firstone.greenjangteo.product.exception.ErrorCode;
import com.firstone.greenjangteo.product.exception.ProductException;
import com.firstone.greenjangteo.product.repository.CategoryRepository;
import com.firstone.greenjangteo.product.repository.ProductImageRepository;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@AllArgsConstructor
public class ProductService {

    private final ProductImageService productImageService;
    private final CategoryService categoryService;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;

    public Map<String, Object> saveProduct(ProductDto productDto, List<String> productImageUrlList, String productImageLocation) throws Exception {
        Product product = Product.addProduct(productDto);
        productRepository.save(product);

        for (int i = 0; i < productImageUrlList.size(); i++) {
            ProductImage productImage = ProductImage.saveProductImage(product, productImageUrlList.get(i), i);
            productImageService.saveProductImage(product, productImage, productImageUrlList.get(i), i, productImageLocation);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("productId", product.getId());
        result.put("createdAt", product.getCreatedAt());

        return result;
    }

    @Transactional(readOnly = true)
    public List<ProductListDto> getProductList() {
        if (productRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> products = productRepository.findAll();
        ProductListDto productListDto = new ProductListDto();
        List<ProductListDto> productList = new ArrayList<>();

        for (Product product : products) {
            Long curProductId = product.getId();

            List<ProductImage> productImage = productImageRepository.findByProductId(curProductId);
            List<String> urlList = new ArrayList<>();
            for (ProductImage image : productImage) {
                urlList.add(image.getUrl());
            }

            List<Category> category = categoryRepository.findByProductId(curProductId);
            List<String> categoryList = new ArrayList<>();
            for (Category value : category) {
                categoryList.add(value.getCategoryName());
            }

            productList.add(productListDto.of(product, urlList, categoryList));
        }
        return productList;
    }

    @Transactional(readOnly = true)
    public ProductListDto getProductDetail(Long productId) {
        Product products = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        ProductListDto productListDto = new ProductListDto();

        List<ProductImage> productImage = productImageRepository.findByProductId(productId);
        List<String> urlList = new ArrayList<>();
        for (ProductImage image : productImage) {
            urlList.add(image.getUrl());
        }

        List<Category> category = categoryRepository.findByProductId(productId);
        List<String> categoryList = new ArrayList<>();
        for (Category value : category) {
            categoryList.add(value.getCategoryName());
        }

        return productListDto.of(products, urlList, categoryList);
    }

    public ResponseEntity updateProduct(Long productId, ProductDto productDto, List<String> productImageUrlList, List<String> categoryList, String productImageLocation) throws Exception {
        //product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        product.setModifiedAt(LocalDateTime.now());
        product.updateProduct(productDto);

        //image
        productImageService.updateProductImage(productId, product, productImageUrlList, productImageLocation);

        //category
        categoryService.updateCategory(productId, product, categoryList);
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity removeProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));

        categoryRepository.deleteByProductId(product.getId());
        productImageRepository.deleteByProductId(product.getId());
        productRepository.deleteById(product.getId());

        return ResponseEntity.noContent().build();
    }
}