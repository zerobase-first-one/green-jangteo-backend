package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.product.form.UpdateProductForm;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private Store sellerId;
    private String name;
    private Integer price;
    private String description;
    private int averageScore;
    private int inventory;
    private int salesRate;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static ProductDto of(Product product) {
        return ProductDto.builder()
                .productId(product.getId())
                .sellerId(product.getStore())
                .name(product.getName())
                .price(product.getPrice())
                .inventory(product.getInventory())
                .createdAt(product.getCreatedAt())
                .modifiedAt(product.getModifiedAt())
                .build();
    }

    public static ProductDto updateProductRequestDtoToProductDto(Product product, UpdateProductForm updateProductForm){
        return ProductDto.builder()
                .productId(product.getId())
                .sellerId(product.getStore())
                .name(updateProductForm.getProductName())
                .price(updateProductForm.getPrice())
                .inventory(updateProductForm.getInventory())
                .description(updateProductForm.getDescription())
                .createdAt(product.getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
