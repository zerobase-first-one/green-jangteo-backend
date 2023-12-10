package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDto {

    private Product product;
    private List<String> url;
    private List<String> category;

    public ProductListDto of(Product product, List<String> productImageUrl, List<String> category) {

        return ProductListDto.builder()
                .product(product)
                .url(productImageUrl)
                .category(category)
                .build();
    }
}
