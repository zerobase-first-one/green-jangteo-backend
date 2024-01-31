package com.firstone.greenjangteo.product.domain.dto.response;

import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.dto.ImageDto;
import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailResponseDto {
    private String productName;
    private int price;
    private int count;
    private CategoryDto categories;
    private List<ImageDto> images;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int reviewCount;
    private List<ReviewsResponseDto> review;
    private String description;

    public static ProductDetailResponseDto descriptionOf(Product product, CategoryDto categoryDetailDto, List<ImageDto> images) {
        return ProductDetailResponseDto.builder()
                .productName(product.getName())
                .price(product.getPrice())
                .count(product.getInventory())
                .categories(categoryDetailDto)
                .images(images)
                .description(product.getDescription())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }

    public static ProductDetailResponseDto reviewsOf(List<ReviewsResponseDto> reviews) {
        return ProductDetailResponseDto.builder()
                .reviewCount(reviews.size())
                .review(reviews)
                .build();
    }
}
