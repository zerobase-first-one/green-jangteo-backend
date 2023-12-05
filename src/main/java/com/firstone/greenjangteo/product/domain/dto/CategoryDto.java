package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Product product;
    private List<String> category;

    public static CategoryDto of(Product product, List<String> category) {
        return CategoryDto.builder()
                .product(product)
                .category(category)
                .build();
    }
}
