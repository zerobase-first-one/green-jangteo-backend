package com.firstone.greenjangteo.product.domain.dto;

import com.firstone.greenjangteo.product.domain.model.Category;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDetailDto {
    private Long categoryId;
    private String firstCategory;
    private String secondCategory;

    public static CategoryDetailDto of(Category category){
        return CategoryDetailDto.builder()
                .categoryId(category.getId())
                .firstCategory(category.getFirstCategory())
                .secondCategory(category.getSecondCategory())
                .build();
    }
}
