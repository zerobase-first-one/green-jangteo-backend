package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product extends BaseEntity {

    /**
     * `id`	BIGINT	NOT NULL,
     * `seller_id`	BIGINT	NOT NULL,
     * `name`	VARCHAR(20)	NOT NULL, >> itemName(?)
     * `price`	INT	NOT NULL,
     * `description`	MEDIUMTEXT	NULL,
     * `average_score`	INT	NULL,
     * `count`	INT	NOT NULL,
     * `sales_rate`	INT	NOT NULL,
     * `created_at`	DATETIME	NOT NULL,
     * `modified_at`	DATETIME	NOT NULL
     */
    @Id
    @Column(name = "product_id") //product_id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //상품코드

    @Column(nullable = false)
    private Long storeId;

    @Column(nullable = false, length = 20)
    private String name; //상품명

    @Column(name = "price", nullable = false)
    private int price; //가격

    private String description;
    private int averageScore;

    @Column(nullable = false)
    private int inventory; //재고

    @Column(nullable = false)
    private int salesRate; //할인율

    public void updateProduct(ProductDto productDto) {
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.inventory = productDto.getInventory();
        this.salesRate = productDto.getSalesRate();
        this.averageScore = productDto.getAverageScore();
        this.description = productDto.getDescription();
    }

    public void subCount(int demand) throws Exception {
        int curCount = this.inventory - demand;
        if (curCount < 0) {
            throw new Exception("상품 재고가 부족합니다. (현 재고량 : " + this.inventory + ")");
        }
        this.inventory = curCount;
    }

    public void addCount(int supply) {
        this.inventory += supply;
    }
}
