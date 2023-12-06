package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private Long sellerId;
    private String name;
    private Integer price;
    private String description;
    private int averageScore;
    private int inventory;
    private int salesRate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

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
