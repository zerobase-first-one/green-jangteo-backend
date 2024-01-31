package com.firstone.greenjangteo.order.dto.response;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductToOrderResponseDto {
    private Long productId;
    private String name;
    private String imageUrl;

    public static ProductToOrderResponseDto from(Product product) {
        ProductImage productImage = product.getProductImages().get(0);
        return new ProductToOrderResponseDto(product.getId(), product.getName(), productImage.getUrl());
    }
}
