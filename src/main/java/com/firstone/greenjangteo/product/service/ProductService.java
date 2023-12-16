package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ImageDto;
import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import com.firstone.greenjangteo.product.domain.dto.ProductImageDto;
import com.firstone.greenjangteo.product.domain.dto.ReviewDto;
import com.firstone.greenjangteo.product.domain.dto.response.AddProductResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductDetailResponseDto;
import com.firstone.greenjangteo.product.domain.dto.response.ProductsResponseDto;
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

    public AddProductResponseDto saveProduct(AddProductForm addProductForm) throws Exception {
        Store store = storeService.getStore(addProductForm.getUserId());
        Product product = Product.addProductRequestDtoToProduct(addProductForm, store);
        productRepository.save(product);

        List<ProductImageDto> imageList = addProductForm.getImages();
        String imagePath = addProductForm.getImageStoragePath();

        for (int i = 0; i < imageList.size(); i++) {
            productImageService.saveProductImage(product, imageList.get(i).getUrl(), i, imagePath);
        }
        categoryService.saveCategory(product.getId(), addProductForm.getCategories());
        return AddProductResponseDto.of(product);
    }

    @Transactional(readOnly = true)
    public Product getProduct(Long productId) {
        Product products = productRepository.findById(productId)
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        return products;
    }

    @Transactional(readOnly = true)
    public List<ProductsResponseDto> getProductList() {
        if (productRepository.findAll().isEmpty()) {
            return new ArrayList<>();
        }

        List<Product> productList = productRepository.findAll();
        List<ProductsResponseDto> products = new ArrayList<>();

        for (int i = 0; i < productList.size(); i++) {
            Long curProductId = productList.get(i).getId();

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

            products.add(ProductsResponseDto.of(productList.get(i), urlList.get(0), categoryList));
        }
        return products;
    }

    @Transactional(readOnly = true)
    public ProductDetailResponseDto getProductDescription(Long productId) {
        Product products = productRepository.findById(productId).orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        ProductDetailResponseDto productDetailResponseDto = new ProductDetailResponseDto();

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

        return productDetailResponseDto.descriptionOf(products, categoryList, urlList);
    }

    public void updateProduct(UpdateProductForm updateProductForm) throws Exception {
        Product product = productRepository.findById(updateProductForm.getProductId())
                .orElseThrow(() -> new ProductException(ErrorCode.PRODUCT_IS_NOT_FOUND));
        ProductDto productDto = ProductDto.updateProductRequestDtoToProductDto(product, updateProductForm);
        product.updateProduct(productDto);

        productImageService.updateProductImage(product.getId(), product, updateProductForm.getImages(), updateProductForm.getImageStoragePath());
        for (int i = 0; i < updateProductForm.getImages().size(); i++) {
            productImageService.saveProductImage(product, updateProductForm.getImages().get(i).getUrl(),
                    updateProductForm.getImages().get(i).getPosition(), updateProductForm.getImageStoragePath());
        }

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