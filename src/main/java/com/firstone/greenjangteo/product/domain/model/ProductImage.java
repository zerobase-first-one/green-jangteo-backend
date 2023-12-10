package com.firstone.greenjangteo.product.domain.model;

import lombok.*;

import javax.persistence.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_image")
public class ProductImage {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private int position;

    public static ProductImage saveProductImage(Product product, String imageUrl, int position) {
        return ProductImage.builder()
                .product(product)
                .url(imageUrl)
                .position(position)
                .build();
    }
}
