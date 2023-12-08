package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.product.domain.dto.ProductDto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @Column(name = "product_id") //product_id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //상품코드

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Store store;

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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime modifiedAt;

    public static Product addProduct(ProductDto productDto) {
        return Product.builder()
                .store(productDto.getSellerId())
                .name(productDto.getName())
                .averageScore(productDto.getAverageScore())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .inventory(productDto.getInventory())
                .salesRate(productDto.getSalesRate())
                .createdAt(LocalDateTime.now())
                .build();
    }

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
