package com.firstone.greenjangteo.product.domain.dto.search;

import lombok.Getter;

import java.util.List;

@Getter
public class ProductSaveAllRequest {
    private List<ProductSaveRequest> productSaveRequestList;
}
