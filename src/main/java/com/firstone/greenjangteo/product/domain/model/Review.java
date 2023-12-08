package com.firstone.greenjangteo.product.domain.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "review")
public class Review extends BaseEntity {

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //review_id

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Long user_id;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Long product_id;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "score", nullable = false)
    private int score;
}
