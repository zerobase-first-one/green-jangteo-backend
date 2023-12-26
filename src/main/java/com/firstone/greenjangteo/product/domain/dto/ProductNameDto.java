package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.document.ProductDocument;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductNameDto {
    private String productName;

    public static ProductNameDto of(ProductDocument productDocument){
        return ProductNameDto.builder()
                .productName(productDocument.getName())
                .build();
    }
}
