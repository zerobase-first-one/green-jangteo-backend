package com.firstone.greenjangteo.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.domain.model.Review;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDto {

    @JsonProperty("productId")
    private Long productId;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("categories")
    private List<String> categories;

    @JsonProperty("price")
    private int price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("images")
    private List<String> images;

    public static ProductListDto of(Product product, List<String> images, List<String> categories) {
        return ProductListDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .categories(categories)
                .price(product.getPrice())
                .description(product.getDescription())
                .images(images)
                .build();
    }
}
