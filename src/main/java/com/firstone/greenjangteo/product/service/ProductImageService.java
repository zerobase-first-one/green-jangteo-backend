package com.firstone.greenjangteo.product.service;

import com.firstone.greenjangteo.product.domain.dto.ProductImageDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import com.firstone.greenjangteo.product.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final FileService fileService;

    public void saveProductImage(Product product, String productImageUrl, int position, String productImageLocation) throws Exception {
        if (!StringUtils.isEmpty(productImageUrl)) {
            String imageName = fileService.uploadFile(productImageLocation, productImageUrl, productImageUrl.getBytes());
            String imageUrl = "/images/product/" + imageName;
            ProductImage productImage = ProductImageDto.toProductImage(product, imageUrl, position);
            productImageRepository.save(productImage);
        }
    }

    public void updateProductImage(Long productId, Product product, List<ProductImageDto> productImageUrlList, String productImageLocation) throws Exception {
        if (!productImageUrlList.isEmpty()) {
            List<ProductImage> savedProductImage = productImageRepository.findByProductId(productId);

            if (!StringUtils.isEmpty(savedProductImage.get(0))) {
                for (int i = 0; i < savedProductImage.size(); i++) {
                    String fileName = savedProductImage.get(i).getUrl().split("/")[3];
                    fileService.deleteFile(productImageLocation, fileName);
                }
            }

            for (int i = 0; i < productImageUrlList.size(); i++) {
                String imageName = fileService.uploadFile(productImageLocation, productImageUrlList.get(i).getUrl(), productImageUrlList.get(i).getUrl().getBytes());
                String imageUrl = "/images/product/" + imageName;
                ProductImage productImage = ProductImage.builder()
                        .product(product)
                        .url(imageUrl)
                        .position(i)
                        .build();
                productImageRepository.save(productImage);
            }
        }
    }
}
