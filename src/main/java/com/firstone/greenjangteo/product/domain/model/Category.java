package com.firstone.greenjangteo.product.domain.model;


import com.firstone.greenjangteo.product.domain.dto.CategoryDto;
import com.firstone.greenjangteo.product.domain.dto.ProductDto;
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
    private Long id; // category id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 20)
    private String categoryName; // category name

    @Column(name = "level", nullable = false)
    private int level; // category level

    public static Category of(Product product, String categoryName, int level) {
        return Category.builder()
                .product(product)
                .categoryName(categoryName)
                .level(level)
                .build();
    }
}
