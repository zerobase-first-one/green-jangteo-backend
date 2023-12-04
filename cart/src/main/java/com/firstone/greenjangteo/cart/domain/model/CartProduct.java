package com.example.greenjangteo.cart.domain.model;

import com.example.greenjangteo.product.domain.model.Product;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * `id`	BIGINT	NOT NULL,
 * `cart_id`	BIGINT	NOT NULL,
 * `product_id`	BIGINT	NOT NULL,
 * `quantity`	INT	NOT NULL,
 * `created_at`	DATETIME	NOT NULL,
 * `modified_at`	DATETIME	NOT NULL
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "cart_product")
public class CartProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_product_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedBy
    private LocalDateTime modifiedAt;
}
