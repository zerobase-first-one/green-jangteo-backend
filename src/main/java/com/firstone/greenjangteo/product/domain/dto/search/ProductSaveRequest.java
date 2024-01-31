package com.firstone.greenjangteo.product.domain.dto.search;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductSaveRequest {
    private Long userId;
    private Long sellerId;
    private Long categoryId;
    private List<String> images;
    private String name;
    private int price;
    private int inventory;
    private String description;
}
