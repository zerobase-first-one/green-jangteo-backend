package com.firstone.greenjangteo.product.domain.dto.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchCondition {
    private Long id;
    private String name;
    private int price;
    private String description;
    private Long categoryId;
    private Long storeId;
}
