package com.firstone.greenjangteo.product.domain.model;

import com.firstone.greenjangteo.product.domain.dto.search.ProductSaveRequest;
import com.firstone.greenjangteo.product.form.AddProductForm;
import com.firstone.greenjangteo.user.domain.store.model.entity.Store;
import com.firstone.greenjangteo.product.domain.dto.ProductDto;
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
@Table(name = "product")
public class Product {
    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Store store;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

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

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    public static Product of(ProductSaveRequest productSaveRequest, Store store) {
        return Product.builder()
                .store(store)
                .name(productSaveRequest.getName())
                .description(productSaveRequest.getDescription())
                .price(productSaveRequest.getPrice())
                .averageScore(builder().averageScore)
                .inventory(productSaveRequest.getInventory())
                .category(Category.builder().id(productSaveRequest.getCategoryId()).build())
                .salesRate(builder().salesRate)
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
                .category(Category.builder().id(addProductForm.getCategoryId()).build())
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
}
