package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.ProductImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {
    private Product product;
    private String url;
    private int position;
    public static ProductImage toProductImage(Product product, String url, int position) {
        return ProductImage.builder()
                .product(product)
                .url(url)
                .position(position)
                .build();
    }
}

