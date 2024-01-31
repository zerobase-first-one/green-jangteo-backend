package com.firstone.greenjangteo.product.domain.dto.request;

import lombok.*;

import java.util.List;

@Getter
public class AddProductRequestDto {
    private Long userId;
    private String productName;
    private String storeName;
    private Long storeId;
    private Long categoryId;
    private int inventory;
    private int price;
    private String description;
    private String imageStoragePath;
    private List<String> images;
}
