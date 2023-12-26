package com.firstone.greenjangteo.product.domain.model;


import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "category")
public class Category {
    @Id
    @Column(name = "category_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstCategory;

    private String secondCategory;

    public static Category of(CategoryDto categoryDto) {
        return Category.builder()
                .firstCategory(categoryDto.getFirstCategory())
                .secondCategory(categoryDto.getSecondCategory())
                .build();
    }
}
