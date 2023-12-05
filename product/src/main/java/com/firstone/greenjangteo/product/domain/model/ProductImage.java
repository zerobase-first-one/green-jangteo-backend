package com.firstone.greenjangteo.product.domain.model;

import lombok.*;

import javax.persistence.*;


/**
 * 	`id`	BIGINT	NOT NULL,
 * 	`product_id`	BIGINT	NOT NULL,
 * 	`url`	VARCHAR(255)	NOT NULL,
 * 	`position`	INT	NOT NULL
 * */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="product_image")
public class ProductImage {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="product_id")
    private Product product;

    @Column(nullable= false)
    private String url;

    @Column(nullable= false)
    private int position;

    public void saveProductImage(Product product, String imageUrl, int position){
        this.product = product;
        this.url = imageUrl;
        this.position = position;
    }
}
