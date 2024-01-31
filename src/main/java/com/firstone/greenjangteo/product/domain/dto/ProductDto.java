package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Category;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private Long sellerId;
    private String name;
    private Integer price;
    private Category category;
    private String description;
    private int averageScore;
    private int inventory;
    private int salesRate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductDto of(Product product) {
        return ProductDto.builder()
                .productId(product.getId())
                .sellerId(product.getStore().getSellerId())
                .name(product.getName())
                .price(product.getPrice())
                .category(product.getCategory())
                .inventory(product.getInventory())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }

    public static ProductDto updateProductRequestDtoToProductDto(Product product, UpdateProductForm updateProductForm){
        return ProductDto.builder()
                .productId(product.getId())
                .sellerId(product.getStore().getSellerId())
                .name(updateProductForm.getProductName())
                .price(updateProductForm.getPrice())
                .inventory(updateProductForm.getInventory())
                .category(Category.builder().id(updateProductForm.getCategoryId()).build())
                .description(updateProductForm.getDescription())
                .createdAt(product.getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
