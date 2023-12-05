package com.firstone.greenjangteo.product.domain.model;


import lombok.*;

import javax.persistence.*;

/**
 * `id`	BIGINT	NOT NULL,
 * `product_id`	BIGINT	NOT NULL,
 * `name`	VARCHAR(20)	NOT NULL,
 * `level`	INT	NOT NULL
 */
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

    public void saveCategory(Product product, String categoryName, int level) {
        this.product = product;
        this.categoryName = categoryName;
        this.level = level;
    }
}
