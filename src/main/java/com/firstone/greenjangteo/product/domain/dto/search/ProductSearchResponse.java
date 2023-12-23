package com.firstone.greenjangteo.product.domain.dto.search;

import com.firstone.greenjangteo.product.domain.document.ProductDocument;
import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.dto.ImageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ProductSearchResponse {
    private Long id;
    private String name;
    private int price;
    private CategoryDto category;
    private List<ImageDto> images;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductSearchResponse from(ProductDocument productDocument){
        return ProductSearchResponse.builder()
                .id(productDocument.getId())
                .name(productDocument.getName())
                .price(productDocument.getPrice())
                .category(productDocument.getCategory())
                .images(productDocument.getImages())
                .description(productDocument.getDescription())
                .createdAt(productDocument.getCreatedAt())
                .modifiedAt(productDocument.getModifiedAt())
                .build();
    }
}
