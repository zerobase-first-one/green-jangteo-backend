package com.firstone.greenjangteo.cart.domain.model;

import com.firstone.greenjangteo.user.model.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * `id`	BIGINT	NOT NULL,
 * 	`user_id`	BIGINT	NOT NULL,
 * 	`order_Id`	BIGINT	NOT NULL,
 * 	`created_at`	DATETIME	NOT NULL,
 * 	`modified_at`	DATETIME	NOT NULL
 * );
 * */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name="cart")
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cart_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @CreatedBy
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    public static Cart createCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);

        return cart;
    }
}
