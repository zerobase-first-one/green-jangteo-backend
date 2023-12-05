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

        //상품 등록
        Product product = Product.builder()
                .storeId(productDto.getSellerId())
                .name(productDto.getName())
                .averageScore(productDto.getAverageScore())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .inventory(productDto.getInventory())
                .salesRate(productDto.getSalesRate())
                .build();

        product.setCreatedAt(LocalDateTime.now());
        productRepository.save(product);

        //이미지 등록
        for (int i = 0; i < productImageUrlList.size(); i++) {
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .url(productImageUrlList.get(i))
                    .position(i)
                    .build();
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

        for (int i = 0; i < products.size(); i++) {
            Long curProductId = products.get(i).getId();

            List<ProductImage> productImage = productImageRepository.findByProductId(curProductId);
            List<String> urlList = new ArrayList<>();
            for (int j = 0; j < productImage.size(); j++) {
                urlList.add(productImage.get(j).getUrl());
            }

            List<Category> category = categoryRepository.findByProductId(curProductId);
            List<String> categoryList = new ArrayList<>();
            for (int j = 0; j < category.size(); j++) {
                categoryList.add(category.get(j).getCategoryName());
            }

            productList.add(productListDto.of(products.get(i), urlList, categoryList));
        }
        return productList;
    }

    @Transactional(readOnly = true)
    public ProductListDto getProductDetail(Long productId) {
        Product products = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        ProductListDto productListDto = new ProductListDto();

        List<ProductImage> productImage = productImageRepository.findByProductId(productId);
        List<String> urlList = new ArrayList<>();
        for (int j = 0; j < productImage.size(); j++) {
            urlList.add(productImage.get(j).getUrl());
        }

        List<Category> category = categoryRepository.findByProductId(productId);
        List<String> categoryList = new ArrayList<>();
        for (int j = 0; j < category.size(); j++) {
            categoryList.add(category.get(j).getCategoryName());
        }

        return productListDto.of(products, urlList, categoryList);
    }

    public ResponseEntity.BodyBuilder updateProduct(Long productId, ProductDto productDto, List<String> productImageUrlList, List<String> categoryList, String productImageLocation) throws Exception {
        //product
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        product.setModifiedAt(LocalDateTime.now());
        product.updateProduct(productDto);

        //image
        productImageService.updateProductImage(productId, product, productImageUrlList, productImageLocation);

        //category
        categoryService.updateCategory(productId, product, categoryList);
        return ResponseEntity.status(204);
    }

    public ResponseEntity.BodyBuilder removeProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));

        categoryRepository.deleteByProductId(product.getId());
        productImageRepository.deleteByProductId(product.getId());
        productRepository.deleteById(product.getId());

        return ResponseEntity.status(204);
    }
}