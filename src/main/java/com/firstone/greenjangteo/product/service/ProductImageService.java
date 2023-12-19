package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ProductImageDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import com.firstone.greenjangteo.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepository;

    public void saveProductImage(Product product, String productImageUrl, int position) {
        ProductImage productImage = ProductImageDto.toProductImage(product, productImageUrl, position);
        productImageRepository.save(productImage);
    }

    public void updateProductImage(Long productId, List<ProductImageDto> productImageUrlList) {
        if (!productImageUrlList.isEmpty()) {
            List<ProductImage> savedProductImage = productImageRepository.findByProductId(productId);
            savedProductImage.stream().map(ProductImage::getId).forEach(productImageRepository::deleteById);
            IntStream.range(0, productImageUrlList.size()).mapToObj(i -> ProductImage.saveProductImage(savedProductImage.get(i).getProduct(),
                    productImageUrlList.get(i).getUrl(), productImageUrlList.get(i).getPosition())).forEach(productImageRepository::save);
        }
    }
}
