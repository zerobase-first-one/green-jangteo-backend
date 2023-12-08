package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.user.model.entity.User;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review {

    /**
     * `id`	BIGINT	NOT NULL,
     * `user_id`	BIGINT	NOT NULL,
     * `product_id`	BIGINT	NOT NULL,
     * `content`	VARCHAR(255)	NOT NULL,
     * `score`	INT	NOT NULL,
     * `created_at`	DATETIME	NOT NULL,
     * `modified_at`	DATETIME	NOT NULL
     */

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //review_id

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<User> users;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> products;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "score", nullable = false)
    private int score;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;
}
