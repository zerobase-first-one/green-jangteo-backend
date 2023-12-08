package com.firstone.greenjangteo.user.domain.store.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.product.domain.model.Product;
import com.firstone.greenjangteo.user.domain.store.dto.StoreRequestDto;
import com.firstone.greenjangteo.user.domain.store.model.StoreName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

import static javax.persistence.CascadeType.*;

@Entity(name = "store")
@Table(name = "store")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Store extends BaseEntity {
    @Id
    private Long sellerId;

    @Column(nullable = false, unique = true, length = 20)
    @Convert(converter = StoreName.StoreNameConverter.class)
    private StoreName storeName;

    @Column
    private String description;

    @Column
    private String imageUrl;

    @OneToMany(mappedBy = "store", cascade = {PERSIST, MERGE, REMOVE}, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Product> products;

    private Store(Long userId, String storeName) {
        sellerId = userId;
        this.storeName = StoreName.of(storeName);
    }

    public static Store of(Long userId, String storeName) {
        return new Store(userId, storeName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(sellerId, store.sellerId) && Objects.equals(storeName, store.storeName)
                && Objects.equals(description, store.description) && Objects.equals(imageUrl, store.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId, storeName, description, imageUrl);
    }

    public void update(StoreRequestDto storeRequestDto) {
        storeName = StoreName.of(storeRequestDto.getStoreName());
        description = storeRequestDto.getDescription();
        imageUrl = storeRequestDto.getImageUrl();
    }
}
