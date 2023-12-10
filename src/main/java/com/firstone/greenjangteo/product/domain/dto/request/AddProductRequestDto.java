package com.firstone.greenjangteo.product.domain.dto.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddProductRequestDto {
    private Long userId;
    private String productName;
    private List<String> categories;
    private int inventory;
    private int price;
    private String description;
    private String imageStoragePath;
    private List<String> images;

    public static AddProductRequestDto of(Long userId, String productName, List<String> categories,
                                          int inventory, int price, String description, String imageStoragePath,
                                          List<String> images) {
        return AddProductRequestDto.builder()
                .userId(userId)
                .productName(productName)
                .categories(categories)
                .inventory(inventory)
                .price(price)
                .description(description)
                .imageStoragePath(imageStoragePath)
                .images(images)
                .build();
    }
}
