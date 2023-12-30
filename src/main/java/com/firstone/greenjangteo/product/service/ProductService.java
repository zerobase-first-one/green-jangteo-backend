package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.*;
import com.firstone.greenjangteo.product.domain.dto.response.AddProductResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductDetailResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductsResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ReviewsResponseDto;
import com.firstone.greenjangteo.product.domain.model.Category;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import com.firstone.greenjangteo.product.domain.model.Review;
import com.firstone.greenjangteo.product.exception.ErrorCode;
import com.firstone.greenjangteo.product.exception.ProductRelatedException;
import com.firstone.greenjangteo.product.form.AddProductForm;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import com.firstone.greenjangteo.product.repository.CategoryRepository;
import com.firstone.greenjangteo.product.repository.ProductImageRepository;
import com.firstone.greenjangteo.product.repository.ProductRepository;
import com.firstone.greenjangteo.product.repository.ProductReviewRepository;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.user.domain.store.service.StoreService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    private final StoreService storeService;

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final ProductReviewRepository productReviewRepository;

    public AddProductResponseDto saveProduct(AddProductForm addProductForm) {
        Store store = storeService.getStore(addProductForm.getUserId());
        Product product = Product.addProductRequestDtoToProduct(addProductForm, store);
        productRepository.save(product);

        List<ProductImageDto> imageList = addProductForm.getImages();

        for (int i = 0; i < imageList.size(); i++) {
            productImageService.saveProductImage(product, imageList.get(i).getUrl(), i);
        }

        return AddProductResponseDto.of(product);
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductRelatedException(ErrorCode.PRODUCT_IS_NOT_FOUND.getDescription()));
    }

    @Transactional(readOnly = true)
    public List<ProductsResponseDto> getProductList(int page, int size) {
        if (productRepository.findAll().isEmpty()) {
            throw new ProductRelatedException(ErrorCode.PRODUCT_IS_NOT_FOUND.getDescription());
        }

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Product> productList = productRepository.findAll(pageRequest);
        List<ProductsResponseDto> products = new ArrayList<>();

        for (Product product : productList) {
            Long curProductId = product.getId();

            List<ProductImage> productImage = productImageRepository.findByProductId(curProductId);
            List<String> urlList = new ArrayList<>();
            for (ProductImage image : productImage) {
                urlList.add(image.getUrl());
            }

            products.add(ProductsResponseDto.of(product, urlList.get(0), CategoryDto.of(product.getCategory())));
        }
        return products;
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDescription(Long productId) {
        Product products = productRepository.findById(productId).orElseThrow(() -> new ProductRelatedException(ErrorCode.PRODUCT_IS_NOT_FOUND.getDescription()));

        Optional<Category> category = categoryRepository.findById(products.getCategory().getId());
        CategoryDto categoryDetailDto = CategoryDto.of(category.get());

        List<ProductImage> productImage = productImageRepository.findByProductId(productId);
        List<ImageDto> urlList = new ArrayList<>();
        for (ProductImage image : productImage) {
            urlList.add(ImageDto.toImageDto(image));
        }

        return ProductDetailResponseDto.descriptionOf(products, categoryDetailDto, urlList);
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductReviews(Long productId) {
        Optional<Product> product = productRepository.findById(productId);
        if (product.isEmpty()) throw new ProductRelatedException(ErrorCode.PRODUCT_IS_NOT_FOUND.getDescription());
        List<Review> reviews = productReviewRepository.findAllByProduct(product.get());
        List<ReviewsResponseDto> reviewsResponseDtoList = new ArrayList<>();
        for (Review review : reviews) {
            reviewsResponseDtoList.add(ReviewsResponseDto.from(review));
        }
        return ProductDetailResponseDto.reviewsOf(reviewsResponseDtoList);
    }

    public void updateProduct(UpdateProductForm updateProductForm) {
        Product product = productRepository.findById(updateProductForm.getProductId())
                .orElseThrow(() -> new ProductRelatedException(ErrorCode.PRODUCT_IS_NOT_FOUND.getDescription()));
        ProductDto productDto = ProductDto.updateProductRequestDtoToProductDto(product, updateProductForm);
        product.updateProduct(productDto);
        productImageService.updateProductImage(product.getId(), updateProductForm.getImages());
    }

    public void removeProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductRelatedException(ErrorCode.PRODUCT_IS_NOT_FOUND.getDescription()));

        product.setStore(null);

        productImageRepository.deleteByProductId(product.getId());
        productRepository.deleteById(product.getId());
    }
}
