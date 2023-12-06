package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainProductDto {
    private String productName;
    private String imageUrl;
    private List<String> category;
    private int price;

    public MainProductDto of(String productName, String imageUrl, List<String> category, int price) {
        return MainProductDto.builder()
                .productName(productName)
                .imageUrl(imageUrl)
                .category(category)
                .price(price)
                .build();
    }
}
