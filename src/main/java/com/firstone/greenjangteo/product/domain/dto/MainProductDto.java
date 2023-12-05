package com.firstone.greenjangteo.product.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MainProductDto {
    private String productName;
    private String url;
    private int price;
}
