package com.firstone.greenjangteo.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetailDto {
    @JsonProperty("category")
    private String category;
    @JsonProperty("level")
    private int level;
}