package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.product.form.AddProductForm;
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
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Store store;

    @Column(nullable = false, length = 20)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String description;

    private int averageScore;

    @Column(nullable = false)
    private int inventory;

    private int salesRate;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    public static Product of(ProductDto productDto) {
        return Product.builder()
                .store(productDto.getSellerId())
                .name(productDto.getName())
                .description(productDto.getDescription())
                .price(productDto.getPrice())
                .inventory(productDto.getInventory())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public static Product addProductRequestDtoToProduct(AddProductForm addProductForm, Store store) {
        return Product.builder()
                .store(store)
                .name(addProductForm.getProductName())
                .description(addProductForm.getDescription())
                .price(addProductForm.getPrice())
                .inventory(addProductForm.getInventory())
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();
    }

    public void updateProduct(ProductDto productDto) {
        this.name = productDto.getName();
        this.price = productDto.getPrice();
        this.inventory = productDto.getInventory();
        this.description = productDto.getDescription();
        this.modifiedAt = productDto.getModifiedAt();
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
