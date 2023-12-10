package com.firstone.greenjangteo.user.domain.store.dto;

import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
@Builder
@Getter
public class StoreResponseDto {
    private String storeName;
    private String description;
    private String imageUrl;
    private List<StoreProductDto> storeProductDtos;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static StoreResponseDto from(Store store) {
        List<StoreProductDto> storeProductDtos = Optional.ofNullable(store.getProducts())
                .map(StoreResponseDto::transferStoreProductsToDto)
                .orElse(new ArrayList<>());

        return StoreResponseDto.builder()
                .storeName(store.getStoreName().getValue())
                .description(store.getDescription())
                .imageUrl(store.getImageUrl())
                .storeProductDtos(storeProductDtos)
                .createdAt(store.getCreatedAt())
                .modifiedAt(store.getModifiedAt())
                .build();
    }

    private static List<StoreProductDto> transferStoreProductsToDto(List<Product> storeProducts) {
        return storeProducts.stream()
                .map(StoreProductDto::from)
                .collect(Collectors.toList());
    }
}
