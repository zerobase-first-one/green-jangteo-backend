package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long sellerId;
    private String name;
    private Integer price;
    private String description;
    private int averageScore;
    private int inventory;
    private int salesRate;

    public ProductDto of(Product product) {
        return ProductDto.builder()
                .sellerId(product.getStoreId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .averageScore(product.getAverageScore())
                .inventory(product.getInventory())
                .salesRate(product.getSalesRate())
                .build();
    }
}
