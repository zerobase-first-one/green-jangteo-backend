package com.firstone.greenjangteo.user.domain.store.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Getter
public class StoreProductDto {
    private Long productId;
    private String productName;
    private int price;
    private String imageUrl;
    private int averageScore;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static StoreProductDto from(Product product) {
        return StoreProductDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .price(product.getPrice())
                .imageUrl(product.getProductImages().get(0).getUrl())
                .averageScore(product.getAverageScore())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }
}
