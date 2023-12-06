package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.ProductImage;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImageDto {
    private List<String> url;
    private List<Integer> position;

    public ProductImageDto of(List<String> url, List<Integer> position) {
        return ProductImageDto.builder()
                .url(url)
                .position(position)
                .build();
    }
}

