package com.firstone.greenjangteo.product.domain.dto.response;

import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductsResponseDto {

    private Long productId;
    private String productName;
    private CategoryDto categories;
    private int price;
    private String description;
    private String image;

    public static ProductsResponseDto of(Product product, String image, CategoryDto categories) {
        return ProductsResponseDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .categories(categories)
                .price(product.getPrice())
                .description(product.getDescription())
                .image(image)
                .build();
    }
}
