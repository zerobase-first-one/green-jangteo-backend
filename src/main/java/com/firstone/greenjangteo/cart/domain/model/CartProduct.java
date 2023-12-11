package com.firstone.greenjangteo.cart.domain.model;

import com.firstone.greenjangteo.product.domain.model.Product;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cart_product")
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

    @LastModifiedBy
    @Column(nullable = false)
    private LocalDateTime modifiedAt;


    public static CartProduct cartProductModifiedOf(CartProduct cartProduct, int quantity) {
        return CartProduct.builder()
                .id(cartProduct.getId())
                .cart(cartProduct.getCart())
                .product(cartProduct.getProduct())
                .quantity(quantity)
                .createdAt(cartProduct.getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static CartProduct cartProductCreatedOf(Cart cart, Product product, int quantity){
        return CartProduct.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }
}
