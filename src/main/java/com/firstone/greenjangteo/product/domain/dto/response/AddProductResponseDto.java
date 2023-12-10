package com.firstone.greenjangteo.product.domain.dto.response;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductResponseDto {
    private Long productId;
    private LocalDateTime createdAt;
    public static AddProductResponseDto of(Product product) {
        return AddProductResponseDto.builder()
                .productId(product.getId())
                .createdAt(product.getCreatedAt())
                .build();
    }
}