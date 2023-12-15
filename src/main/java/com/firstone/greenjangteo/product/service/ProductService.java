package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ImageDto;
import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import com.firstone.greenjangteo.product.domain.dto.ProductImageDto;
import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.dto.response.AddProductResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductDetailResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductsResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewsResponseDto;
import com.firstone.greenjangteo.product.domain.model.Category;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import com.firstone.greenjangteo.product.exception.ErrorCode;
import com.firstone.greenjangteo.product.exception.ProductException;
import com.firstone.greenjangteo.product.form.AddProductForm;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import com.firstone.greenjangteo.product.repository.CategoryRepository;
import com.firstone.greenjangteo.product.repository.ProductImageRepository;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.product.repository.ReviewRepository;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@AllArgsConstructor
public class ProductService {

    private final ProductImageService productImageService;
    private final CategoryService categoryService;
    private final StoreService storeService;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ReviewRepository reviewRepository;

    public AddProductResponseDto saveProduct(AddProductForm addProductForm) {
        Store store = storeService.getStore(addProductForm.getUserId());
        Product product = Product.addProductRequestDtoToProduct(addProductForm, store);
        productRepository.save(product);

        List<ProductImageDto> imageList = addProductForm.getImages();

        for (int i = 0; i < imageList.size(); i++) {
            productImageService.saveProductImage(product, imageList.get(i).getUrl(), i);
        }
        categoryService.saveCategory(product.getId(), addProductForm.getCategories());
        return AddProductResponseDto.of(product);
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public List<ProductsResponseDto> getProductList() {
        if (productRepository.findAll().isEmpty()) {
            throw new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND);
        }

        List<Product> productList = productRepository.findAll();
        List<ProductsResponseDto> products = new ArrayList<>();

        for (Product product : productList) {
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

            products.add(ProductsResponseDto.of(product, urlList.get(0), categoryList));
        }
        return products;
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDescription(Long productId) {
        Product products = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));

        List<Category> category = categoryRepository.findByProductId(productId);
        List<String> categoryList = new ArrayList<>();
        for (Category value : category) {
            categoryList.add(value.getCategoryName());
        }

        List<ProductImage> productImage = productImageRepository.findByProductId(productId);
        List<ImageDto> urlList = new ArrayList<>();
        for (ProductImage image : productImage) {
            urlList.add(ImageDto.toImageDto(image));
        }

        return ProductDetailResponseDto.descriptionOf(products, categoryList, urlList);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductReviews(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) throw new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND);
        List<ReviewDto> reviews = reviewRepository.findAllByProduct(product.get());
        List<ReviewsResponseDto> reviewsResponseDtoList = new ArrayList<>();
        for (ReviewDto review : reviews) {
            reviewsResponseDtoList.add(ReviewsResponseDto.of(review));
        }
        return ProductDetailResponseDto.reviewsOf(reviewsResponseDtoList);
    }

    public void updateProduct(UpdateProductForm updateProductForm) {
        Product product = productRepository.findById(updateProductForm.getProductId())
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        ProductDto productDto = ProductDto.updateProductRequestDtoToProductDto(product, updateProductForm);
        product.updateProduct(productDto);
        productImageService.updateProductImage(product.getId(), updateProductForm.getImages());
        categoryService.updateCategory(product.getId(), product, updateProductForm.getCategories());
    }

    public void removeProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));

        product.setStore(null);

        categoryRepository.deleteAllByProductId(product.getId());
        productImageRepository.deleteByProductId(product.getId());
        productRepository.deleteById(product.getId());
    }
}