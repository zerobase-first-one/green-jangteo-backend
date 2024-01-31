package com.firstone.greenjangteo.product.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.firstone.greenjangteo.product.domain.model.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDto {
    @JsonProperty("firstCategory")
    private String firstCategory;
    @JsonProperty("secondCategory")
    private String secondCategory;

    public static CategoryDto of(Category category){
        return CategoryDto.builder()
                .firstCategory(category.getFirstCategory())
                .secondCategory(category.getSecondCategory())
                .build();
    }
}