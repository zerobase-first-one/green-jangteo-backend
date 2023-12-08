package com.firstone.greenjangteo.user.domain.store.service.model.entity;

import com.firstone.greenjangteo.audit.BaseEntity;
import com.firstone.greenjangteo.user.domain.store.dto.StoreDto;
import com.firstone.greenjangteo.user.domain.store.service.model.StoreName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Objects;

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
        return Objects.equals(sellerId, store.sellerId) && Objects.equals(storeName, store.storeName) && Objects.equals(description, store.description) && Objects.equals(imageUrl, store.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sellerId, storeName, description, imageUrl);
    }

    public void update(StoreDto storeDto) {
        storeName = StoreName.of(storeDto.getStoreName());
        description = storeDto.getDescription();
        imageUrl = storeDto.getImageUrl();
    }
}
